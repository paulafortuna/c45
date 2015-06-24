import java.util.Comparator;

/**
 * Created by Paula on 5/2/2015.
 */
public class InstanceValues{

    double[] values;
    //TO DO pass class to string?

    public InstanceValues(String[] temp) {
        values = new double[temp.length];
        for (int i = 0; i < temp.length; i++) {
            values[i] = Double.parseDouble(temp[i]);
        }
    }

    public double valueAtPosition(int pos) {
        return values[pos];
    }

    double getClassValue(){
        return values[values.length-1];
    }
}
