import java.util.ArrayList;

/**
 * Created by Paula on 5/3/2015.
 */
public class State {

    public enum Enum {
        MINOR_EQ, MAJOR
    }

    int bestAttribute;
    double divisionPoint;
    double gainRatio;
    double info;
    boolean infoReady;
    boolean end;
    Enum side;
    Node father;
    DataSet[] resDS;

    State(){
        resDS = new DataSet[2];
        end = false;
        infoReady = false;
        side = Enum.MINOR_EQ;
    }

    public State(State st, Enum en) {
        this.resDS = new DataSet[2];
        this.end = false;
        this.infoReady = false;
        this.father = st.father;
        this.side = en;
    }
}
