import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Node {
    int depth;
    String attribute;
    int attributePos;
    double division_point;
    State.Enum positionRelativeToFather;
    HashMap<Double, Double> leafFrequencies;
    ArrayList<Node> descendants;
    double majorityClass;

    public Node(String attribute, int attributePos, double limit_value, Node father, State.Enum st) {
        this.attribute = attribute;
        this.division_point = limit_value;
        this.positionRelativeToFather = st;
        this.descendants = new ArrayList<Node>();
        this.attributePos = attributePos;
        this.depth = 0;
    }

    void setLeafFrequencies(HashMap<Double, Double> leafFrequencies){
        this.leafFrequencies = leafFrequencies;
    }

    void addDescendant(Node d){
        d.depth = this.depth;
        d.depth++;
        descendants.add(d);
    }

    boolean hasDescendants(){
        return !descendants.isEmpty();
    }

    public void setMajorityClass(double majorityClass) {
        this.majorityClass = majorityClass;
    }


    public void changeToLeaf() {
        descendants.clear();
    }
}
