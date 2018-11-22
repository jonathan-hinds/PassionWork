import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Map;

public class Main2 {

    public static void main(String[] args) {
        try{
            FileWriter fw = new FileWriter("TF-IDF.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);
            pw.print("Here is some example text.");
            pw.printf("\n\n\n\n\n");
            pw.flush();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
