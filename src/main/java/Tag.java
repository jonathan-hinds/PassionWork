public class Tag {

    private String word;
    private String POS;
    private int amount;

    public Tag(String word, String POS, int amount) {
        this.word = word;
        this.POS = POS;
        this.amount = amount;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getPOS() {
        return POS;
    }

    public void setPOS(String POS) {
        this.POS = POS;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
