import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import org.apache.commons.logging.LogFactory;
import edu.stanford.nlp.util.logging.RedwoodConfiguration;
import com.google.re2j.Matcher;
import com.google.re2j.Pattern;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.util.*;
import java.util.logging.Level;

public class Scraper {

    private String                  url     = "";
    private String                  output  = "";

    private ArrayList<String>       filter  = new ArrayList<>();
    private ArrayList<Tag>          tags    = new ArrayList<>();
    private ArrayList<String>       PDF     = new ArrayList<>();

    private Map<String, Frequency>  count   = new HashMap<>();
    private Map<String, Integer>    links   = new HashMap<>();
    private Map<String, String>     pos     = new HashMap<>();

    private StanfordCoreNLP pipeline;

    public Scraper(String url, String output){
        this.url = url; this.output = output;
    }

    public String findPOS(String input){
        Properties props = new Properties();

        props.setProperty("annotators", "tokenize, ssplit, pos");
        this.pipeline = new StanfordCoreNLP(props);

        Annotation document = new Annotation(input);
        this.pipeline.annotate(document);
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);

        for(CoreMap sentence: sentences) {
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                return pos;
            }
        }
        return null;
    }

    public String parsePOS(String word){

        if(word != null) {

            switch (word) {
                case "CD":
                    return ("Cardinal number");

                case "DT":
                    return ("Determiner");

                case "EX":
                    return ("Existential there");

                case "FW":
                    return ("Foreign word");

                case "IN":
                    return ("Preposition or subordinating conjunction");

                case "JJ":
                    return ("Adjective");

                case "JJR":
                    return ("Adjective");

                case "JJS":
                    return ("Adjective");

                case "LS":
                    return ("List item marker");

                case "MD":
                    return ("Modal");

                case "NN":
                    return ("Noun");

                case "NNS":
                    return ("Noun");

                case "NNP":
                    return ("Noun");

                case "PDT":
                    return ("Predeterminer");

                case "POS":
                    return ("Possessive ending");

                case "PRP":
                    return ("Personal pronoun");

                case "PRP$":
                    return ("Possessive pronoun");

                case "RB":
                    return ("Adverb");

                case "RBR":
                    return ("Adverb");

                case "RBS":
                    return ("Adverb");

                case "RP":
                    return ("Particle");

                case "SYM":
                    return ("Symbol");

                case "TO":
                    return ("To");

                case "UH":
                    return ("Interjection");

                case "VB":
                    return ("Verb");

                case "VBD":
                    return ("Verb");

                case "VBN":
                    return ("Verb");

                case "VBZ":
                    return ("Verb");

                case "VBP":
                    return ("Verb");

                case "VBG":
                    return ("Verb");

                case "WDT":
                    return ("Wh-determiner");

                case "WP":
                    return ("Wh-pronoun");

                case "WP$":
                    return ("Possessive wh-pronoun");

                case "WRB":
                    return ("Wh-adverb");
            }
        }

            return null;
    }

    public void getPOS(){
        for(String key : this.count.keySet()){
            String pos = findPOS(key);
            if(pos != null) {
                this.pos.put(key, pos);
            }
        }
    }

    public void getCountFromUrl(){
        String xml = getXML(this.url);
        if(xml != null) {
            getLinksXML(xml);
            getPDFXML(xml);
            Map<String, Frequency> count = wordCount(xml);
            this.count = count;
        } else {
            System.out.println("No page text found");
        }
    }

    public ArrayList<String> getTags(int topAmount){
        this.tags = new ArrayList<>();
        ArrayList<String> words = new ArrayList<>();
        Map<String, Frequency> replace = this.count;
        for(int i = 0; i < topAmount; i ++) {
            Tag tag = null;
            int max = 0;
            String result = "";
            for (String key : replace.keySet()) {
                if (count.get(key).getTf() > max) {
                    max = count.get(key).getTf();
                    result = key;
                }
            }
            if(this.count.size() > 0) {
                tag = new Tag(result, parsePOS(findPOS(result)), replace.get(result).getTf());
                words.add(result);
                this.tags.add(tag);
                replace.remove(result, replace.get(result));
            }
        }
        return words;
    }

    public ArrayList<String> getTags(int topAmount, String ... includes){
        if(this.count.size() > 0) {
            this.tags = new ArrayList<>();
            ArrayList<String> words = new ArrayList<>();
            Map<String, Frequency> replace = this.count;
            while (words.size() < topAmount) {
                Tag tag = null;
                int max = 0;
                String result = "";
                for (String key : replace.keySet()) {
                    if (count.get(key).getTf() > max) {
                        max = count.get(key).getTf();
                        result = key;
                    }
                }
                if (parsePOS(findPOS(result)) != null) {
                    for (String goodPOS : includes) {
                        if (parsePOS(findPOS(result)).equalsIgnoreCase(goodPOS)) {
                            tag = new Tag(result, parsePOS(findPOS(result)), replace.get(result).getTf());
                            words.add(result);
                            this.tags.add(tag);
                            break;
                        }
                    }
                }
                replace.remove(result, replace.get(result));
                if(replace.size() == 0){ break; }
            }
        }
        return null;
    }

    public void printTags(){
        if(this.tags.size() > 0) {
            try {
                FileWriter fw = new FileWriter(this.output, true);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter pw = new PrintWriter(bw);
                if (this.tags.size() > 0) {
                    pw.printf("%-15s",java.time.LocalTime.now());
                    for (Tag tag : this.tags) {
                        pw.printf("%-40s", "[" + tag.getWord() + " ( " + tag.getPOS() + " ) " + "( " + tag.getAmount() + " )] ");
                    }
                    pw.print(this.url + " --------------- " + this.tags.size() + "\n");
                    pw.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            for (Tag tag : this.tags) {
                System.out.printf("%-30s", "[" + tag.getWord() + " ( " + tag.getPOS() + " ) " + "( " + tag.getAmount() + " )] ");
            }
        }
    }

    public String getXML(String url){
        WebClient client = getClient();
        Page page = null;
        String searchUrl = this.url;
        try {

            page = client.getPage(searchUrl);
            String xml = getPageSource(page);
            System.out.println("Successfull Status Code for: " + url);
            return xml;
        }catch(MalformedURLException e){
            System.out.println("Malformed URL in" + url);
        }catch(FailingHttpStatusCodeException e){
            System.out.println("Failed Status Code for: " + url);
        }catch(IOException e){
            System.out.println("IOERROR");
        }catch(NoClassDefFoundError e){

        }
        return null;
    }

    public String filter(String input){
        for(String filter : this.filter){
            input = input.replaceAll("(\\s" + filter + "\\s)", " ");
        }
        return input;
    }

    public void getLinksXML(String xml){
        ArrayList<String> links = getRegExList(xml, "https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)");
        for(String link : links){
            if(!this.links.containsKey(link)){
                this.links.put(link, 1);
            } else {
                Integer amount = this.links.get(link);
                amount ++;
                this.links.put(link, amount);
            }
        }
    }

    public WebClient getClient(){
        WebClient client = new WebClient();
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);
        client.setRefreshHandler(new ThreadedRefreshHandler());
        return client;
    }

    public void setFilter(){
        try {
            Iterator<String> si = new Scanner(new FileReader("./src/main/resources/filter.txt"));
            while(((Scanner) si).hasNextLine()){
                String word = si.next();
                word = word.toLowerCase();
                word = word.trim();
                this.filter.add(word);
            }
        } catch (FileNotFoundException e) {
            System.out.println("./src/main/resources/filter.txt" + " Does Not Exist");
            System.exit(-1);
        }
    }

    public Map<String, Frequency> wordCount(String XML){
        Map<String, Frequency> words = new HashMap<>();
        if(XML != null) {
            String text = parseHTML(XML);
            String filtered = filter(text);
            ArrayList<String> refineWords = getRegExList(filtered, "\\w+");
            for (String words1 : refineWords) {
                if (!words.containsKey(words1)) {
                    Frequency fq = new Frequency();
                    fq.setTf(1);
                    words.put(words1, fq);
                } else {

                    Integer amount = words.get(words1).getTf();
                    amount++;
                    words.get(words1).setTf(amount);
                    words.put(words1, words.get(words1));
                }
            }
        }
        return words;
    }

    public ArrayList<String> getRegExList(String input, String regex){
        ArrayList<String> words = new ArrayList<String>();
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        while(matcher.find()){
            String word = matcher.group();
            words.add(word);
        }
        return words;
    }

    public void printOrderAsc(){
        this.count.entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getValue))
                .forEach(e ->{
                    String word = e.toString();
                    String[] pair = word.split("=");
                    word = pair[0];
                    Integer amount = Integer.valueOf(pair[1]);
                    System.out.printf("%-50s%-12d \n", word, amount);
                });
    }

    public void printLinksAsc(){
        this.links.entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getValue))
                .forEach(e ->{
                    String word = e.toString();
                    String[] pair = word.split("=");
                    System.out.println(pair[0]);
                });
    }

    public Map<String, Integer> getLinks() {
        return links;
    }

    public static String getPageSource(Page page) {
        if(page instanceof HtmlPage)
        {
            return ((HtmlPage)page).asXml();
        }
        else if(page instanceof TextPage)
        {
            return ((TextPage)page).getContent();
        }
        else
        {
            return null;
        }
    }

    public String parseHTML(String html){
        Document doc = Jsoup.parse(html);
        return doc.text();
    }

    public void disableLogs(){
        LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
        RedwoodConfiguration.current().clear().apply();
        Set<String> loggers = new HashSet<>(Arrays.asList("org.apache.http", "groovyx.net.http"));
    }

    public void downloadPDF(String input, String filename, String directory){
        HttpDownloadUtility http = new HttpDownloadUtility();
        try {
            http.downloadFile(input, directory, filename + ".pdf");
            checkPDF2(directory, filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getPDFXML(String xml){
        ArrayList<String> links = getRegExList(xml, "https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*\\.pdf)");
        for(String pdf : links){
            if(!this.PDF.contains(pdf)){
                System.out.println("Found: " + pdf);
                this.PDF.add(pdf);
            }
        }
    }

    public ArrayList<String> getPDFs(){
        return this.PDF;
    }

    public void deleteFile(String fileName){
        File file = new File(fileName + ".pdf");
        if(file.delete()){
            System.out.println(fileName + " has been deleted because it could not open.");
        } else {
            System.out.println(fileName + ".pdf" + " was not downloaded and could not be deleted.");
        }
    }

    public void checkPDF2(String saveDir, String filename){
        //Loading an existing document
        File file = new File(saveDir + filename + ".pdf");
        PDDocument document = null;
        try {

            document = PDDocument.load(file);
            //PDFRenderer renderer = new PDFRenderer(document);
            //BufferedImage image = renderer.renderImage(0);
            //ImageIO.write(image, "PNG", new File("/Users/jonathanhinds/Projects/hinds/PDF/" + filename + ".png"));
            String text = getPDFString(document);
            PrintWriter pw = new PrintWriter(new FileWriter(saveDir + filename + ".txt"));
            pw.write(text);
            document.close();

            System.out.println(filename + ": properly formatted.");
        } catch (IOException e) {
            deleteFile(saveDir + filename);
        }
    }

    public ArrayList<Tag> tags(){
        return this.tags;
    }

    public boolean makeDirectory(String filename){
        return new File("/Users/jonathanhinds/Projects/hinds/IMGs/" + filename).mkdirs();
    }















    public void getTagsPDF(String src) {
        File folder = new File(src);
        File[] fileEntry = folder.listFiles();
        for(File file : fileEntry) {
            System.out.println("\n\n" + file.getName() + "\n\n");
            String text = getPDFString(file);
            count = wordCount(text);
            for (File file2 : fileEntry) {
                Map<String, Frequency> map = wordCount(getPDFString(file2));
                for (String word : count.keySet()) {
                    if (map.containsKey(word)) {
                        int DF = count.get(word).getDf();
                        DF++;
                        count.get(word).setDf(DF);
                    }
                }
            }
            for (String key : count.keySet()) {
                int tf = count.get(key).getTf();
                int df = count.get(key).getTf();
                double tfidf = tf * Math.log((double)fileEntry.length / (double)df);
                if(tfidf > 15) {
                    System.out.println(key + " - TF: " + count.get(key).getTf() + " DF: " + count.get(key).getDf() + " TF-IDF: " + tfidf);
                }
            }
        }
    }

    public String getPDFString(File file){
        try {
            if(!file.getName().equals(".DS_Store")) {
                PDDocument pdd = PDDocument.load(file);
                PDFTextStripper stripper = new PDFTextStripper();
                String text = stripper.getText(pdd);
                return text;
            }
        }catch(IOException e){

        }
        return null;
    }

    public String getPDFString(PDDocument doc){
        String text = null;
        try {
            PDFTextStripper stripper = new PDFTextStripper();
            text = stripper.getText(doc);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text;
    }
}

