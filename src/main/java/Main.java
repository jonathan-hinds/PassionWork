//todo - "... list of 100 key terms from... " -  50,000  "... sites, you can present them to the user and let them click 5
//todo - or something. Then you take those 5 and use cosine similaity WITH EACH OF THE KEY TERMS FOR EACH SITE and  you'll
//todo - get a score beween 0 and 1, 1 being a perfect match and 0 meaning nothing is returned"

//todo - consider converting the text to lemma using stanford NLP
//todo - find a way to normalize the value of the TF-IDF score.
//todo - research lemminization, cosine similarity,

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        Scraper scraper = new Scraper("http://wstein.org/papers/icms/icms_2010.pdf", "tags.txt");
        //scraper.downloadPDF("http://www.uv.es/pla/Quixote/Kinematics2.pdf", "test", "/Users/jonathanhinds/Projects/hinds/PDF/");
        //scraper.getPDF("http://www.cs.ucr.edu/~nael/pubs/micro16.pdf", "somethingnew");
        String src = "/Users/jonathanhinds/Projects/hinds/PDF/";
        scraper.setFilter();
        scraper.disableLogs();
        //scraper.getTagsPDF(src);
        //scraper.downloadPDF("http://wstein.org/papers/icms/icms_2010.pdf", "TEST", "/Users/jonathanhinds/Projects/hinds/PDF/");
        ArrayList<Map<String, Map<String, Frequency>>> docStat = scraper.getTagsPDF(src);
        for(Map<String, Map<String, Frequency>> map : docStat)
        {
            for(Map.Entry<String, Map<String, Frequency>> urlMap : map.entrySet())
            {
                String url = urlMap.getKey();
                Map<String, Frequency> docMap = urlMap.getValue();
                try{
                    FileWriter fw = new FileWriter("TF-IDF.txt", true);
                    BufferedWriter bw = new BufferedWriter(fw);
                    PrintWriter pw = new PrintWriter(bw);
                    docMap.entrySet().stream()
                            .sorted(Comparator.comparingDouble(o -> o.getValue().getTfidf()))
                            .forEach(e -> {
                                String word = e.getKey();
                                String POS = scraper.parsePOS(scraper.findPOS(word));
                                double tfidf = e.getValue().getTfidf();
                                double norm_tfidf = e.getValue().getNormalTFIDF();
                                if(norm_tfidf > .1) {
                                    pw.printf("%-15s", java.time.LocalTime.now());
                                    pw.printf("%-50s %-30s %-25s %10.20f %10.20f \n", url, word, POS, tfidf, norm_tfidf);
                                    pw.flush();
                                }
                            });
                    System.out.println("End of doc " + url);
                    pw.printf("\n\n\n\n\n");
                    pw.flush();
                }catch(Exception e){}
            }
        }
        System.out.println("Total documents: " + docStat.size());











        //scraper.printOrderAsc();
        //3:50

//        String[] href = {"https://en.wikipedia.org/wiki/Convolutional_neural_network",
//                "https://en.wikipedia.org/wiki/Deep_learning",
//                "https://en.wikipedia.org/wiki/Machine_learning",
//                "https://en.wikipedia.org/wiki/Artificial_neural_network",
//                "https://en.wikipedia.org/wiki/Artificial_neuron",
//                "https://en.wikipedia.org/wiki/Artificial_intelligence",
//                "https://en.wikipedia.org/wiki/Data_science"};
//
//        ArrayList<String> URLS = new ArrayList<>();
//
//
//        //for (String s : href) {
//
//            //url passed into constructor
//            Scraper crawl = new Scraper("https://en.wikipedia.org/wiki/Convolutional_neural_network", "tags.txt");
//            //to disable HTMLUnit logging
//            crawl.disableLogs();
//            //enable filter - imported from /resources/filter.txt
//            crawl.setFilter();
//            //get the count of words from the page specified in the url
//            //get the links from the page specified in the url
//            crawl.getCountFromUrl();
//            //print the links ascending by times shown.
//            //crawl.printLinksAsc();
//            //print words ascending by times shown.
//            crawl.printOrderAsc();
//            //store the top 10 tags
//            //crawl.getTags(10);
////          //print these tags
//            //crawl.printTags();
//
//            for (String key : crawl.getLinks().keySet()) {
//                if (!URLS.contains(key)) {
//                    //System.out.println("First add " + key);
//                    URLS.add(key);
//                } else {
//                    //System.out.println("Duplicate URL " + key);
//                }
//
//            }
////
//////
//            //repeat this process for every link that was found on the last crawl.
//            for (String links : crawl.getLinks().keySet()) {
//                Scraper scraper = new Scraper(links, "tags2.txt");
//                scraper.setFilter();
//                scraper.getCountFromUrl();
//                //scraper.getTags(10);
//                //scraper.printTags();
//                scraper.printOrderAsc();
//                for (String key : scraper.getLinks().keySet()) {
//                    if (!URLS.contains(key)) {
//                        //System.out.println("First add " + key);
//                        URLS.add(key);
//                    } else {
//                        //System.out.println("Duplicate URL " + key);
//                    }
//
//                }
//            }
////
////            for (String links : URLS) {
////                Scraper scraper = new Scraper(links, "tags2.txt");
////                scraper.setFilter();
////                scraper.getCountFromUrl();
////                //scraper.printLinksAsc();
////                scraper.getTags(10);
////                scraper.printTags();
////            }
//
//            //processes has ended.
//            System.out.println("Complete");
//        //}
    }
}
