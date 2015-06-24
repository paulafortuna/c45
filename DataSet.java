import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by Paula on 5/2/2015.
 */
public class DataSet {

    String[] attributesNames;
    ArrayList<InstanceValues> instances;
    int columnNum;
    int attributesNum;
    int classPos;
    Double majorityClass;
    HashSet<Double> classesSet;

    public DataSet() {
        columnNum = 0;
        instances = new ArrayList<InstanceValues>();
        classesSet = new HashSet<Double>();
    }

    void addInstance(InstanceValues instance) {
        instances.add(instance);
    }

    protected boolean allInstancesHaveSameClass() {
        //put each instance in a set -> hash set for constant search
        HashSet<Double> set = new HashSet<Double>(); //melhorar eficiencia
        for (int i = 0; i < instances.size(); i++) {

            if (!set.contains(instances.get(i).valueAtPosition(classPos))){
                set.add(instances.get(i).valueAtPosition(classPos));
                if(set.size() > 1)
                    return false;
            }
        }
        return true;
    }

    void readCSV(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        int j = 0;
        while ((line = reader.readLine()) != null) {
            if(j == 0){ //read first line with attributes names
                attributesNames = line.split(",");
                columnNum = attributesNames.length;
                attributesNum = columnNum - 1;
                classPos = columnNum - 1;
            }
            else{ // read data and put it in the instances list
                String[] temp = line.split(",");
                InstanceValues i = new InstanceValues(temp);
                instances.add(i);
                classesSet.add(i.getClassValue());
            }
            j++;
        }
    }

    public DataSet[] split(int division) { //improve this method
        DataSet ds1 = new DataSet();
        DataSet ds2 = new DataSet();
        ds1.attributesNames = attributesNames;
        ds1.columnNum = columnNum;
        ds1.attributesNum = attributesNum;
        ds1.classPos = classPos;
        ds2.attributesNames = attributesNames;
        ds2.columnNum = columnNum;
        ds2.attributesNum = attributesNum;
        ds2.classPos = classPos;

        for(int i = 0; i <= division; i++)
            ds1.addInstance(instances.get(i));
        for(int i = division + 1; i < instances.size(); i++)
            ds2.addInstance(instances.get(i));

        DataSet[] res = new DataSet[2];
        res[0] = ds1;
        res[1] = ds2;

        return res;
    }

    HashMap<Double, Double> getClassFrequency() {

        HashMap<Double,Double> freq = new HashMap<Double, Double>();
        for(int i = 0; i < instances.size(); i++){
            double value = instances.get(i).valueAtPosition(classPos);
            if(freq.containsKey(value)){
                Double pastOcurrencies = freq.get(value);
                freq.put(value,++pastOcurrencies);
            }
            else{
                freq.put(value,1.0);
            }
        }

        double majority_p = 0.0;
        majorityClass = new Double(0.0);
        //calculate probabilities of each class and apply log
        Iterator it = freq.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry)it.next();
            Double p = (Double) pair.getValue() / instances.size();
            pair.setValue(p);
            if(p > majority_p){
                majority_p = p;
                majorityClass = (Double) pair.getKey();
            }
        }

        return freq;
    }


}
