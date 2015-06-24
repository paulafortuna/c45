import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class C45 {

    Node init;
    static String FILENAME = "out\\ia.csv"; //predict music
    //static String FILENAME = "C:\\Users\\Paula\\IdeaProjects\\IART\\out\\ia2.csv"; // example
    static double TEST_PERCENTAGE = 0.3;
    static int PRUNE_DEPTH = 6;

    public static void main(String[] args) {

        // 1) load csv to dataset
        DataSet dataset;
        dataset = new DataSet();

        try {
            dataset.readCSV(FILENAME);
        } catch (IOException e) {
            e.printStackTrace();
        }

        DataSet[] trainAndTest = splitDataForTrainAndTest(TEST_PERCENTAGE, dataset);

        System.out.println("instance number = " + dataset.instances.size());

        // 2) transform instances in tree
        State st = new State();
        generateTree(st, trainAndTest[0]);

        // 3) results unpruned
        printRules(st.father,"");
        testTree(st, trainAndTest[1], dataset.classesSet);

        // 4) prune tree
        pruneTree(st.father);

        // 5) results pruned
        printRules(st.father,"");
        testTree(st, trainAndTest[1], dataset.classesSet);
    }

    /***************************
     *      2) GENERATE TREE
     ***************************/

    private static void generateTree(State st, DataSet dataset) {

        if(!dataset.allInstancesHaveSameClass()) { //if its not a base case -> //encontrada instância de uma classe não antes vista.??
            findAttributeMaximizeGainRatio(st, dataset);  //find the attribute with maximum gain ratio
            if(st.end == true) {
                addNewDecisionNode(st, dataset);
                return; //for the condition where none attribute increases gain ratio
            }

            addNewDecisionNode(st, dataset); //add new decision node according to attribute and division point
            generateTree(new State(st, State.Enum.MINOR_EQ), st.resDS[0]);
            generateTree(new State(st, State.Enum.MAJOR),st.resDS[1]);
        }
        else{
            addNewDecisionNode(st, dataset);
        }
    }

    private static int findAttributeMaximizeGainRatio(State st, DataSet dataset) {
        double maxGainRatio = 0;
        int posMaxAttribute = 0;
        double maxDivisionPoint = 0;
        DataSet[] maxDS = new DataSet[2];
        double temp_GainRatio;

        for (int i = 0; i < dataset.attributesNum; i++) { //for each attribute
            //System.out.println("attribute pos = " + i);
            temp_GainRatio = C45Formulas.gainRatioA(st, dataset, i);
            if(temp_GainRatio > maxGainRatio) {
                maxGainRatio = temp_GainRatio;
                posMaxAttribute = i;
                maxDivisionPoint = st.divisionPoint;
                maxDS = st.resDS;
            }
        }
        if(maxGainRatio == 0) { //base case -> I earn nothing in applying any of the attributes
            st.end = true;
        }
        st.bestAttribute = posMaxAttribute;
        st.divisionPoint = maxDivisionPoint;
        st.resDS = maxDS;
        return posMaxAttribute;
    }

    private static void addNewDecisionNode(State st, DataSet dataset) {
        //prepare node values
        String attribute = dataset.attributesNames[st.bestAttribute];
        int attributePos = st.bestAttribute;
        double div = st.divisionPoint;
        // create node
        Node new_node = new Node(attribute, attributePos, div, st.father, st.side);
        new_node.setLeafFrequencies(dataset.getClassFrequency());
        new_node.setMajorityClass(dataset.majorityClass);

        // add it to the tree
        if(st.father != null) {
            st.father.addDescendant(new_node);
        }
        st.father = new_node;
    }

    /***************************
     *      2) PRUNE TREE
     ***************************/

    private static void pruneTree(Node node) {
        if(node.hasDescendants()) {
            if (node.depth > PRUNE_DEPTH) {
                node.changeToLeaf();
            }
            else{
                pruneTree(node.descendants.get(0));
                pruneTree(node.descendants.get(1));
            }
        }
    }

    /***************************
     *      4) PRINT TREE
     ***************************/

    private static void printRules(Node root, String previous) {
        if(root != null){
            if(root.hasDescendants()){
                String next;

                if(root.descendants.size() >= 1) {
                    next = previous + " If " + root.attribute + " <= " + root.division_point + " ";
                    printRules(root.descendants.get(0), next);
                }
                if(root.descendants.size() >= 2) {
                    next = previous + " If " + root.attribute + " > " + root.division_point + " ";
                    printRules(root.descendants.get(1), next);
                }
                return;
            }
            else{
                //print classes

                System.out.print(previous);
                Iterator it = root.leafFrequencies.entrySet().iterator();
                while (it.hasNext()) {
                    HashMap.Entry pair = (HashMap.Entry)it.next();
                    System.out.print(" class " + pair.getKey() + " has relative frequency " + pair.getValue());
                }
                System.out.println();
            }
        }
    }

    /***************************
     *      5) TEST TREE
     ***************************/

    private static DataSet[] splitDataForTrainAndTest(double percentage, DataSet dataset) {
        //split dataset in two -> the first one is the training dataset, the second one is the test dataset
        // i is de spliting point
        int i = (int) (dataset.instances.size()*(1 - percentage));
        return dataset.split(i);
    }

    private static void testTree(State st, DataSet dataSet, HashSet<Double> classesSet) {

        int totalErro = 0;

        //create a table with all the classes to save the error for each one
        HashMap<Double, Error> errorTable = new HashMap<Double, Error>();

        Iterator iterator = classesSet.iterator();
        // check values
        while (iterator.hasNext()){
            errorTable.put((Double) iterator.next(), new Error());
        }

        for(int i = 0; i < dataSet.instances.size(); i++){
            //get learn and real class
            double learnClass = getLearnClass(st.father, dataSet.instances.get(i));
            double instanceClass = dataSet.instances.get(i).getClassValue();

            if(learnClass == instanceClass) {
                Iterator it = errorTable.entrySet().iterator();
                while (it.hasNext()) {
                    HashMap.Entry pair = (HashMap.Entry)it.next();
                    if((Double) pair.getKey() == instanceClass){ //for the right class
                        Error temp = (Error) pair.getValue();
                        temp.truePositives++;
                        errorTable.put(learnClass, temp);
                    }
                    else { // for all of the others
                        Error temp = (Error) pair.getValue();
                        temp.trueNegatives++;
                        errorTable.put(learnClass, temp);
                    }
                }
            }
            else{
                totalErro++;
                Iterator it = errorTable.entrySet().iterator();
                while (it.hasNext()) {
                    HashMap.Entry pair = (HashMap.Entry)it.next();
                    if((Double) pair.getKey() == instanceClass){ //for the right class
                        Error temp = (Error) pair.getValue();
                        temp.falseNegatives++;
                        errorTable.put(learnClass, temp);
                    }
                    else if ((Double) pair.getKey() == learnClass){ // for all of the others
                        Error temp = (Error) pair.getValue();
                        temp.falsePositives++;
                        errorTable.put(learnClass, temp);
                    }
                    else{
                        //confirmar
                        Error temp = (Error) pair.getValue();
                        temp.trueNegatives++;
                        errorTable.put(learnClass, temp);
                    }
                }
            }
        }

        //analize error
        analiseError(errorTable, totalErro, dataSet.instances.size());
    }

    private static double getLearnClass(Node father, InstanceValues instance) {
        if(father.hasDescendants()) {
            if (instance.valueAtPosition(father.attributePos) <= father.division_point) {
                return getLearnClass(father.descendants.get(0), instance);
            } else {
                return getLearnClass(father.descendants.get(1), instance);
            }
        }
        return father.majorityClass;
    }

    private static void analiseError(HashMap<Double, Error> results, int totalError, int dataSetSize) {
        //ERROR RATE
        double errorRate = (double) totalError / dataSetSize;
        System.out.println("The error rate is " + errorRate);

        //PRECISION
        //Precision: proportion of instances that are truly of a class divided by the total instances classified as that class

        double sumPrecision = 0.0;
        double sumRecall = 0.0;
        double sumF = 0.0;

        Iterator it = results.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry) it.next();
            Error er = (Error) pair.getValue();
            er.updatePrecision();
            er.updateRecall();
            er.updateF();
            sumPrecision = er.precision;
            sumRecall = er.recall;
            sumF = er.f;
        }

        System.out.println("Precision: " + sumPrecision / results.size());
        System.out.println("Recall: " + sumRecall / results.size());
        System.out.println("F: " + sumF / results.size());
    }

}
