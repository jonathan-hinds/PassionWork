public class Frequency implements Comparable<Frequency> {

    private Integer tf = 0;
    private Integer df = 0;

    public Frequency(){}

    public Frequency(int tf, int df){

    }

    public Integer getTf() {
        return tf;
    }

    public void setTf(int tf) {
        this.tf = tf;
    }

    public Integer getDf() {
        return df;
    }

    public void setDf(int df) {
        this.df = df;
    }

    public int compareTo(Frequency o) {
        return tf.compareTo(o.getTf());
    }
}
