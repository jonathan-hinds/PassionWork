public class Frequency implements Comparable<Frequency> {

    private Integer tf = 0;
    private Integer df = 0;
    private Double normalTF = 0.0;
    private Double normalTFIDF = 0.0;
    private Double tfidf = 0.0;

    public Frequency(){}

    public Frequency(int tf, int df){

    }

    public double getTfidf() {
        return tfidf;
    }

    public void setTfidf(double tfidf) {
        this.tfidf = tfidf;
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
        return tfidf.compareTo(o.getTfidf());
    }

    public Double getNormalTF() {
        return normalTF;
    }

    public void setNormalTF(Double normalTF) {
        this.normalTF = normalTF;
    }

    public Double getNormalTFIDF() {
        return normalTFIDF;
    }

    public void setNormalTFIDF(Double normalTFIDF) {
        this.normalTFIDF = normalTFIDF;
    }
}
