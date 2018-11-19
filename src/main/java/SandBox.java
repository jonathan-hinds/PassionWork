import java.util.ArrayList;

public class SandBox {

    public static void main(String[] args) {


        String[] href03 = {
                "https://en.wikipedia.org/wiki/Neural_network_software",
                "https://en.wikipedia.org/wiki/Artificial_neural_network",
                "https://en.wikipedia.org/wiki/Java",
                "https://en.wikipedia.org/wiki/JavaScript",
                "https://en.wikipedia.org/wiki/Python_(programming_language)",
                "https://en.wikipedia.org/wiki/Python_syntax_and_semantics",
                "https://ehmatthes.github.io/pcc/cheatsheets/README.html",
                "https://www.tutorialspoint.com/spring/spring_overview.htm",
                "https://www.tutorialspoint.com/artificial_intelligence_with_python/index.htm",
                "https://www.dataquest.io/blog/top-20-python-ai-and-machine-learning-open-source-projects/",
                "https://en.wikipedia.org/wiki/Convolution",
                "https://en.wikipedia.org/wiki/Convolutional_neural_network"
        };

        ArrayList<String> href = new ArrayList<>();
        ArrayList<String> href2 = new ArrayList<>();

        for (String link04 : href03) {

            Scraper crawl = new Scraper(link04, "sandBox.txt");
            crawl.disableLogs();
            crawl.setFilter();
            crawl.getCountFromUrl();
            crawl.getTags(10);
            crawl.printTags();

            for (String link : crawl.getLinks().keySet()) {
                System.out.println("Trying link: " + link);
                Scraper scraper = new Scraper(link, "sandBox.txt");
                scraper.disableLogs();
                scraper.setFilter();
                scraper.getCountFromUrl();
                scraper.getTags(10, "noun", "verb", "adverb", "adjective");
                scraper.printTags();
                for (String link03 : scraper.getLinks().keySet()) {
                    if (!href.contains(link03)) {
                        href.add(link03);
                    }
                }
            }

            for (String link02 : href) {
                System.out.println("Trying link: " + link02);
                Scraper scraper = new Scraper(link02, "sandBox.txt");
                scraper.disableLogs();
                scraper.setFilter();
                scraper.getCountFromUrl();
                scraper.getTags(10, "noun", "verb", "adverb", "adjective");
                scraper.printTags();
                for (String link03 : scraper.getLinks().keySet()) {
                    if (!href2.contains(link03)) {
                        href2.add(link03);
                    }
                }
            }
        }
    }
}
