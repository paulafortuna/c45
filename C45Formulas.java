import java.util.HashMap;
import java.util.Iterator;

/**
 * With this class we want to encapsulate the C4.5 formulas.
 * It is important to save from this calculus the two datasets resulting for the best division.
 * We save it in order to do not repeat this calculation.
 */
public class C45Formulas {

    static double gainRatioA(State st, DataSet dataset, int attribute){
        Double s = splitInfoA(dataset, attribute);
        Double g = gainA(st, dataset, attribute);
        // st.gainRatio = gainA(st, dataset, attribute)/splitInfoA(dataset, attribute);
        if(s.equals(0))
            return 0;
        return g/s;
    }

    static double gainA(State st, DataSet dataset, int attribute){
        double info;
        if(st.infoReady)
            info = st.info;
        else
            info = info(st, dataset, dataset.classPos);

        return info - infoA(st, dataset, attribute);
    }

    static double info(State st, DataSet dataset, int attribute){ //entropy
        st.info = calculateEntropy(dataset, dataset.columnNum-1);
        st.infoReady = true;
        return st.info;
    }

    private static double calculateEntropy(DataSet dataset, int attribute) {
        //Info(D) = - sumatory (p* log2(p))
        double sum_res = 0;

        //calculate frequencies of each class
        HashMap<Double,Double> freq = new HashMap<Double, Double>();
        for(int i = 0; i < dataset.instances.size(); i++){
            double value = dataset.instances.get(i).valueAtPosition(attribute);
            if(freq.containsKey(value)){
                Double pastOcurrencies = freq.get(value);
                freq.put(value,++pastOcurrencies);
            }
            else{
                freq.put(value,1.0);
            }
        }

        //calculate probabilities of each class and apply log
        Iterator it = freq.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry)it.next();
            Double p = (Double) pair.getValue() / dataset.instances.size();
            pair.setValue(p*Math.log(p)/Math.log(2));
            sum_res += p*Math.log(p)/Math.log(2);
            //System.out.println(pair.getKey() + " = " + pair.getValue());
            it.remove(); // avoids a ConcurrentModificationException
        }
        return -sum_res;
    }

    static double infoA(State st, DataSet dataset, int attribute){
        return findDivisionPoint(st, dataset, attribute);
    }

    private static double findDivisionPoint(State st, DataSet dataset, int attribute) {
        //sort data by attribute
        SortCriteria function = new SortCriteria(attribute);
        dataset.instances.sort(function);

        //find infoA for each possible division and pick min)
        boolean begin = true;
        int min_pos = 0;
        double min_info = 0;
        for(int i = 0; i < dataset.instances.size()-1; i++){

            if(checkEquals(dataset, i, attribute))
                continue;

            //split dataset according to i
            DataSet[] tempDS = dataset.split(i);
            // evaluate entropy of each side
            double entropy1 = Math.abs(calculateEntropy(tempDS[0], tempDS[0].classPos));
            double entropy2 = Math.abs(calculateEntropy(tempDS[1], tempDS[1].classPos));
            //sum both entropies and compare with min
            double totPond = tempDS[0].instances.size() + tempDS[1].instances.size();
            double tot_entropy = ((double) tempDS[0].instances.size())/totPond*entropy1 + ((double) tempDS[1].instances.size())/totPond*entropy2;
            if(begin) {
                begin = false;
                min_info = tot_entropy;
                min_pos = i;
                st.resDS = tempDS;
                st.bestAttribute = attribute;
                st.divisionPoint = dataset.instances.get(i).valueAtPosition(attribute);
            }
            else if (tot_entropy < min_info) {
                min_info = tot_entropy;
                min_pos = i;
                st.resDS = tempDS;
                st.bestAttribute = attribute;
                st.divisionPoint = dataset.instances.get(i).valueAtPosition(attribute);
            }

        }
        return min_info;
    }

    private static boolean checkEquals(DataSet dataset, int i, int attribute) {
        if(i + 1 < dataset.instances.size()) {
            Double v1 = dataset.instances.get(i + 1).valueAtPosition(attribute);
            Double v2 = dataset.instances.get(i).valueAtPosition(attribute);
            if (v1.equals(v2))
                return true;
        }
        return false;
    }

    static double splitInfoA(DataSet dataset, int attribute){ //entropy
        return calculateEntropy(dataset, attribute);
    }
}
