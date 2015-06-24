import java.util.Comparator;

/**
 * Created by Paula on 5/2/2015.
 */
public class SortCriteria implements Comparator<InstanceValues> {

    int attributePos;

    public SortCriteria(int attributePos) {
        this.attributePos = attributePos;
    }

    @Override
    public int compare(InstanceValues i1,InstanceValues i2) {
        double val1 = i1.valueAtPosition(attributePos);
        double val2 = i2.valueAtPosition(attributePos);
        Double res = new Double(val1 - val2);
        if(res < 0)
            return -1;
        else if(res > 0)
            return 1;

        return 0;
    }

}
