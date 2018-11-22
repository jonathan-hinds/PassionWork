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
import org.apache.pdfbox.text.PDFTextStripper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.*;
import java.net.MalformedURLException;
import java.util.*;

public class Scraper {

    private String                  url     = "";
    private String                  output  = "";
    private ArrayList<String>       filter  = new ArrayList<>();
    private ArrayList<Tag>          tags    = new ArrayList<>();
    private ArrayList<String>       PDF     = new ArrayList<>();
    private Map<String, Frequency>  count   = new HashMap<>();
    private Map<String, Integer>    links   = new HashMap<>();
    private Map<String, String>     pos     = new HashMap<>();
    private StanfordCoreNLP         pipeline;

    public Scraper(String url, String output){
        this.url = url; this.output = output;
    }
    /**
     * @param input - String to be parsed into a part of speach
     * @return - the part of speach code to be parsed into part of speach text
     */
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

    /**
     * @param pos - part of speach code to be parsed into plane text
     * @return - plane text part of speech after code has been pasrsed.
     */
    public String parsePOS(String pos){

        if(pos != null) {

            switch (pos) {
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

    /**
     * Get a list (this.pos) of all of the words in count, parsed into
     * a part of speach code.
     */
    public void getPOS(){
        for(String key : this.count.keySet()){
            String pos = findPOS(key);
            if(pos != null) {
                this.pos.put(key, pos);
            }
        }
    }

    /**
     * - Connect to this.url (url fed into constructor) and get the HTML page
     * - retrieve a list of a links from this html (this.links)
     * - retrieve a list of pdfs from this html (this.pdf)
     * - "No page text found" output if the page is not an html page.
     * - todo - break this up into multiple methods. single responsibility.
     */
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

    /**
     * @param topAmount - amount of tags to retrieve from this of words (this.count)
     * @return - return the words with the highest frequency
     * todo - this has not been updated to use the Frequency object in this.count's value.
     */
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

    /**
     * @param topAmount - amount of tags to retrieve from this of words (this.count)
     * @param includes - a list of strings representing which parts of speach should be included
     *                 int these tags.
     * @return - todo this current returns null only because it is mutating ths this.tags method.
     *
     */
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

    /**
     * print the tags listed in the this.tags list (collected by the method getTags) to
     * the output path that was entered into the constructor.
     */
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
        }
    }

    /**
     * @param url - the url from which the xml is being retrieved from.
     * @return - the XML that was retrieved from the url.
     * todo - I believe at the moment this is using this.url and not the url param.
     */
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

    /**
     * @param input - filter words from a string, using the this.filter list of words
     *              created by setFilter.
     * @return - the input param with the words from the filter list removed from it.
     * todo - instead of using a list, try reading the list form a document - less loops.
     */
    public String filter(String input){
        for(String filter : this.filter){
            input = input.replaceAll("(\\s" + filter + "\\s)", " ");
        }
        return input;
    }

    /**
     * @param xml - the xml from which the links are found. Links are found using a regex pattern
     *            and stored in this.links.
     */
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

    /**
     * @return - create a web client with css and javascript disabled. Using a ThreadedRefreshHandler.
     */
    public WebClient getClient(){
        WebClient client = new WebClient();
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);
        client.setRefreshHandler(new ThreadedRefreshHandler());
        return client;
    }

    /**
     * Read in text from "./src/main/resources/filter.txt" line by line, and add each
     * line into the filter list
     * todo - consider removing this method, and relying on filter to read in the text
     * todo - from the file, and also replace the words it's filtering. Without another
     * todo - ArrayList, and less loops, this could potentially increase time effeciency.
     */
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

    /**
     * @param XML - xml to be taken in as a string. It is then parsed into plane text. Filtered
     *            for unwanted words. broken into an array of words, and analyzed for term frequency.
     *            todo - this method does a lot at once. Try to break this down to keep it more simple.
     * @return - a map of each word, with its term frequency and, and normalized term frequency calculated.
     * - todo - this method is crucual because it collects the word count in the doc. this is essential to
     * - todo - calculating the accurate Term Frequcny. Right now, since the filter is being applied, the word
     * - todo - count is NOT accurate, need to filter words, but include filtered words in total word count.
     */
    public Map<String, Frequency> wordCount(String XML){
        Map<String, Frequency> words = new HashMap<>();
        int wordCount = 0;
        if(XML != null) {
            String text = parseHTML(XML);

            String filtered = filter(text);

            filtered = filtered.toLowerCase();
            //text = text.toLowerCase();

            ArrayList<String> refineWords = getRegExList(filtered, "\\w+");
            for (String words1 : refineWords) {
                wordCount ++;
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

            for(Map.Entry<String, Frequency> map : words.entrySet()){
                map.getValue().setNormalTF((double) map.getValue().getTf() / (double) wordCount);
            }

        }
        return words;
    }

    /**
     * @param input - String taken in to be broken into a list.
     * @param regex - Pattern of to match words which will be incorperated in the returned list.
     * @return - a list of words found in the origin input, that matched the regex pattern param.
     */
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


    /**
     * Print the words in this.count, sorted in ascending order by its tfidf value.
     * - todo - due to the amount of text returned, this is best to be replaced with a print to file.
     */
    public void printOrderAsc(){
        this.count.entrySet().stream()
                .sorted(Comparator.comparingDouble(o -> o.getValue().getTfidf()))
                .forEach(e ->{
                    String word = e.getKey();
                    Double score = e.getValue().getTfidf();
                    System.out.printf("%-50s%-10.10f \n", word, score);
                });
    }


    /**
     * Print the links that were found, when scraping this.url, these links are printed based in ascending
     * order, based on how many times they were found.
     */
    public void printLinksAsc(){
        this.links.entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getValue))
                .forEach(e ->{
                    String word = e.toString();
                    String[] pair = word.split("=");
                    System.out.println(pair[0]);
                });
    }


    /**
     * Setter method for this.links
     * @return a map of the links found while scraping this.url, paired with the amount of times this linked
     * was seen.
     */
    public Map<String, Integer> getLinks() {
        return links;
    }


    /**
     * @param page the web page from which the scraper is ectracting text from.
     * @return a String representation of the XML / HTML scraped from the page, based on what kind of response
     * the page gives.
     */
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


    /**
     * @param html - String representation of an html page, to be stripped of all its html tags.
     * @return - String representation of an html page with all of its html tags stripped form it.
     */
    public String parseHTML(String html){
        Document doc = Jsoup.parse(html);
        return doc.text();
    }

    /**
     * Disable logging gfor Htmlunit.
     */
    public void disableLogs(){
        LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
        RedwoodConfiguration.current().clear().apply();
        Set<String> loggers = new HashSet<>(Arrays.asList("org.apache.http", "groovyx.net.http"));
    }


    /**
     * @param input - file URL from which we are downloading the PDF
     * @param filename - the file name once it is to be saved to a driectory.
     * @param directory - The directory which the pdf file is being saved to.
     */
    public void downloadPDF(String input, String filename, String directory){
        try {
            HttpDownloadUtility.downloadFile(input, directory, filename + ".pdf");
            checkPDF2(directory, filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * @param xml - String representation for the HTML / XML content of a web page, from which the
     *            scraper is extracting links to pdf files, and storing them to a list (this.pdf)
     */
    public void getPDFXML(String xml){
        ArrayList<String> links = getRegExList(xml, "https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*\\.pdf)");
        for(String pdf : links){
            if(!this.PDF.contains(pdf)){
                System.out.println("Found: " + pdf);
                this.PDF.add(pdf);
            }
        }
    }

    /**
     * @return a list of pdfs found during the last crawl.
     */
    public ArrayList<String> getPDFs(){
        return this.PDF;
    }

    /**
     * @param fileName pdf file name to be deleted if it could not be opened, or did not contain any text.
     */
    public void deleteFile(String fileName){
        File file = new File(fileName + ".pdf");
        if(file.delete()){
            System.out.println(fileName + " has been deleted because it could not open.");
        } else {
            System.out.println(fileName + ".pdf" + " was not downloaded and could not be deleted.");
        }
    }

    /**
     * @param saveDir - Directory of file to be checked.
     * @param filename - File to be checked that it can be opened and or parsed into text.
     *                 Only files which should fail as of now (11/22/2018) are pdfs formated
     *                 with strictly images, or pdfs which are password protected.
     * todo - when a pdf is checked, it should be converted into a text file and saved into a
     * todo - directory with the same name as the file. this is so that read times are quicker
     * todo - when analysign the document for frquency values.
     */
    public void checkPDF2(String saveDir, String filename){
        System.out.println(filename);
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
            //deleteFile(saveDir + filename);
        }
    }

    /**
     * This method was created as a proof of concept for the following:
     * Given all the pdfs in directory "/Users/jonathanhinds/Projects/hinds/PDF/" open each
     * and convert to a text (.txt) file saved into a directory named after the file.
     * todo - get the text from a pdf, if no text is found delete the pdf (this means image formatting)
     * todo - add the text to a text file and delete the pdf file file
     * todo - consider using the following to read words from a file Stream.generate(new Scanner(file)::next);
     */
    public void test(){
        int noTextCount = 0;
        File file = new File("/Users/jonathanhinds/Projects/hinds/PDF/");
        try {
            int count = 0;
            for (File file2 : file.listFiles()) {
                System.out.println("DOCUMENT: " + count);
                count ++;
                System.out.println("\n\n" + file2.getName() + "\n\n");

                if(!file2.getName().equals(".DS_Store") && !file2.getName().equals("PDFText")) {

                    PDDocument document = null;
                    //String filename = file2.getName().replaceAll(".pdf", ".txt");
                    document = PDDocument.load(file2);
                    String text = getPDFString(document);
                    if(text.split("\\s+").length <= 1){
                        noTextCount ++;
                    }
                    System.out.println(text);
////                    PrintWriter pw = new PrintWriter(new FileWriter("/Users/jonathanhinds/Projects/hinds/PDF/PDFText/" + filename));
////                    pw.write(text);
////                    document.close();

                }
            }
        }catch(IOException e){

        }
        System.out.println("Total documents formated with images: " + noTextCount + ", Total Documents: " + file.listFiles().length);
    }

    /**
     * Getter method used to retrieve the list of tags found in the last crawl.
     * @return - list of Tag objects found from the text in the last crawl. Represents top N reoccuring words
     */
    public ArrayList<Tag> tags(){
        return this.tags;
    }

    /**
     * @param src directory for which files are anaylized for TF-IDF value
     * @return a list of maps, each map contains <String url, Map<String word, Frequency>>.
     *
     * This method is really heavy right now, and will eventually have to be broken up into several methods.
     * - right now it does the following:
     * - loops through every file in a directory.
     * - for each document, a map is created, which holds <String url <Map String word, Frequncy values>>
     * - for every file (file A), the term frequency is calculated.
     * - loops through the same directory again
     * - for every one of these files (file B), the term frequency is calculated.
     * - loops through the terms in file A, and see if they exist in any of file B, - this finds the DF score.
     * - it then loops through each term found in file A, and calculates the TF-IDF value using the term frequency and document frequcnies found in the previous step.
     * - While doing this, it is also finding the min and max TF-IDF value.
     * - it then loops through each term found in file A, and calculates the Normalized TF-IDF value.
     * - once a file has all of its values calculated, it is added into a list which is returned.
     * - todo - this method is doing too much, take some time to break this into smaller methods.
     * - todo - See if you can find a way to reduce the amount of loops being performed here.
     * - todo - See if you can lammenatize the words in the pdf, this might be good for another method, but this
     * - todo - could potentially increase the accuracy of a the scrapers results. (when trying to find tags)
     */
    public ArrayList<Map<String, Map<String, Frequency>>> getTagsPDF(String src) {
        ArrayList<Map<String, Map<String, Frequency>>> docStat = new ArrayList<>();
        File folder = new File(src);
        File[] fileEntry = folder.listFiles();
        //compare each file here (file1)
        for(File file : fileEntry) {
            count = new HashMap<>();
            if(!file.getName().equals(".DS_Store")) {
                Map<String, Map<String, Frequency>> docMap = new HashMap<>();
                String text = getPDFString(file);
                count = wordCount(text);
                System.out.println("File1: " + count.keySet().size());
                //to each file here (file2)
                for (File file2 : fileEntry) {
                    if (!file.getName().equals(".DS_Store")) {
                        //get the TF of each word in this file (file2)
                        Map<String, Frequency> map = wordCount(getPDFString(file2));
                        //loop through the words found in file1
                        for (String word : count.keySet()) {
                            //if it is also found in file2
                            if (map.containsKey(word)) {
                                //increase the DF by one
                                int DF = count.get(word).getDf();
                                DF++;
                                count.get(word).setDf(DF);
                            }
                        }
                    }
                }

                //min and max tfidf score used to find normalized value.
                double min = 1000000;
                double max = 0;

                //calculate the tfidf once all DF values are found.
                for (String key : count.keySet()) {
                    //int tf = count.get(key).getTf();
                    double tf = count.get(key).getNormalTF();
                    int df = count.get(key).getDf();
                    double tfidf = tf * (Math.log((double) fileEntry.length / (double) df));
                    count.get(key).setTfidf(tfidf);

                    //find the min and max while calculating the tfidf scores to save resources
                    if(tfidf > max){
                        max = tfidf;
                    }
                    if(tfidf < min){
                        min = tfidf;
                    }

                    docMap.put(file.getName(), count);
                }
                //find the normalized tf-idf score once all tf-idf scores are found.
                for(String key2 : count.keySet()){
                    Frequency wordFQ = count.get(key2);
                    wordFQ.setNormalTFIDF((wordFQ.getTfidf() - min)/(max - min));
                }

                docStat.add(docMap);
            }
        }
        return docStat;
    }


    /**
     * @param file - pdf file to be parsed into a string
     * @return - String text content of the original input pdf file.
     */
    public String getPDFString(File file){
        try {
            if(!file.getName().equals(".DS_Store")) {
                PDDocument pdd = PDDocument.load(file);
                PDFTextStripper stripper = new PDFTextStripper();
                stripper.setSortByPosition(true);
                String text = stripper.getText(pdd);
                return text;
            }
        }catch(IOException e){

        }
        return null;
    }


    /**
     * @param doc - a pdf doc loaded using PDFbox - to be converted into plain text as a String
     * @return - String text content of a loaded pdf doc.
     */
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

