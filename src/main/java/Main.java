import com.gargoylesoftware.htmlunit.IncorrectnessListener;
import com.gargoylesoftware.htmlunit.WebClient;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.util.*;

public class Main {

    public static void main(String[] args) {
//
        Scraper scraper = new Scraper("http://wstein.org/papers/icms/icms_2010.pdf", "tags.txt");
        //scraper.downloadPDF("http://www.uv.es/pla/Quixote/Kinematics2.pdf", "test", "/Users/jonathanhinds/Projects/hinds/PDF/");
        //scraper.getPDF("http://www.cs.ucr.edu/~nael/pubs/micro16.pdf", "somethingnew");
        String src = "/Users/jonathanhinds/Projects/hinds/PDF/";
        scraper.setFilter();
        scraper.disableLogs();
        //scraper.getTagsPDF(src);
        scraper.downloadPDF("http://wstein.org/papers/icms/icms_2010.pdf", "TEST", "/Users/jonathanhinds/Projects/hinds/PDF/");



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
