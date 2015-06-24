
public class Error {

    int truePositives;
    int trueNegatives;
    int falsePositives;
    int falseNegatives;

    double precision; //  fraction of retrieved instances that are relevant
    double recall; //Recall: proportion of instances classified as a given class divided by the actual total in that class (equivalent to TP rate)
    double f; //F-Measure: A combined measure for precision and recall calculated as 2 * Precision * Recall / (Precision + Recall)


    void updatePrecision(){
        if( (truePositives + falsePositives) > 0)
            precision = (double) truePositives / (truePositives + falsePositives); //percentagem de certos nos classificados como classe xpto
    }
    void updateRecall(){
        if( (truePositives + falseNegatives) > 0)
            recall = (double) truePositives / (truePositives + falseNegatives); //percentagem de classe em todos bem classificados
    }
    void updateF(){
        if( (precision + recall) > 0)
            f = (double) 2 * precision * recall / (precision + recall);
    }

}
