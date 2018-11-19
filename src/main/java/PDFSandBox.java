import java.util.ArrayList;

public class PDFSandBox {

    public static void main(String[] args) {

        String[] href03 = {
//                "https://en.wikipedia.org/wiki/Neural_network_software",
//                "https://en.wikipedia.org/wiki/Artificial_neural_network",
//                "https://en.wikipedia.org/wiki/Java",
//                "https://en.wikipedia.org/wiki/JavaScript",
//                "https://en.wikipedia.org/wiki/Python_(programming_language)",
//                "https://en.wikipedia.org/wiki/Python_syntax_and_semantics",
//                "https://ehmatthes.github.io/pcc/cheatsheets/README.html",
//                "https://www.tutorialspoint.com/spring/spring_overview.htm",
//                "https://www.tutorialspoint.com/artificial_intelligence_with_python/index.htm",
//                "https://www.dataquest.io/blog/top-20-python-ai-and-machine-learning-open-source-projects/",
//                "https://en.wikipedia.org/wiki/Convolution",
//                "https://en.wikipedia.org/wiki/Convolutional_neural_network",
//                "https://en.wikipedia.org/wiki/Artificial_intelligence",
//                "https://en.wikipedia.org/wiki/Java_syntax",
//                "https://en.wikipedia.org/wiki/Data_science",
//                "https://en.wikipedia.org/wiki/Data_mining",
//                "https://en.wikipedia.org/wiki/Data_structure",
//                "https://www.theguardian.com/science/2013/jul/01/how-algorithms-rule-world-nsa",
//                "https://en.wikipedia.org/wiki/Trachoma",
//                "https://www.fic.nih.gov/News/GlobalHealthMatters/november-december-2012/Pages/eye-institute-trachoma.aspx",
//                "https://www.mayoclinic.org/diseases-conditions/trachoma/diagnosis-treatment/drc-20378509",
//                "https://emedicine.medscape.com/article/1202088-treatment",
                "https://en.wikipedia.org/wiki/List_of_indigenous_peoples",
                "http://www.who.int/topics/health_services_indigenous/en/"
        };

        ArrayList<String> href = new ArrayList<>();
        ArrayList<String> href2 = new ArrayList<>();

        for (String link04 : href03) {
            System.out.println("\n\nStarting SEED: " + link04 + "\n\n");
            Scraper crawl = new Scraper(link04, "sandBox.txt");
            crawl.disableLogs();
            crawl.setFilter();
            crawl.getCountFromUrl();
            crawl.getTags(10, "noun", "verb", "adverb", "adjective");
            for (String pdfURL : crawl.getPDFs()) {
                String name = pdfURL.replaceAll("\\W+", "-");
                crawl.downloadPDF(pdfURL, "[" + name + "]", "/Users/jonathanhinds/Projects/hinds/PDF/");
            }

            for (String link : crawl.getLinks().keySet()) {
                Scraper scraper = new Scraper(link, "sandBox.txt");
                scraper.disableLogs();
                scraper.setFilter();
                scraper.getCountFromUrl();
                scraper.getTags(10, "noun", "verb", "adverb", "adjective");
                for (String pdfURL : scraper.getPDFs()) {
                    String name = pdfURL.replaceAll("\\W+", "-");
                    scraper.downloadPDF(pdfURL, "[" + name + "]", "/Users/jonathanhinds/Projects/hinds/PDF/");
                }
                for (String link03 : scraper.getLinks().keySet()) {
                    if (!href.contains(link03)) {
                        href.add(link03);
                    }
                }
            }

            for (String link02 : href) {
                Scraper scraper = new Scraper(link02, "sandBox.txt");
                scraper.disableLogs();
                scraper.setFilter();
                scraper.getCountFromUrl();
                scraper.getTags(10, "noun", "verb", "adverb", "adjective");
                for (String pdfURL : scraper.getPDFs()) {
                    String name = pdfURL.replaceAll("\\W+", "-");
                    scraper.downloadPDF(pdfURL, "[" + name + "]", "/Users/jonathanhinds/Projects/hinds/PDF/");
                }
            }
        }
    }
}
