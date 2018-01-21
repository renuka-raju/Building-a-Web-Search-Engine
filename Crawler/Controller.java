package crawler;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import edu.uci.ics.crawler4j.crawler.CrawlController;

import java.io.File;
import java.io.FileReader;

public class Controller {
    public static void main(String[] args) throws Exception {
        String crawlStorageFolder = "C:/Users/Renuka/Desktop/pages";
        int numberOfCrawlers =1;
        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(crawlStorageFolder);
        config.setMaxDepthOfCrawling(16);
        config.setMaxPagesToFetch(20000);
        config.setFollowRedirects(true);
        config.setIncludeHttpsPages(true);
        config.setPolitenessDelay(300);
        config.setIncludeBinaryContentInCrawling(true);
        //config.setResumableCrawling(true);
/* Instantiate the controller for this crawl.*/
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        //robotstxtConfig.setEnabled(false);

        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
/* For each crawl, you need to add some seed urls. These are the first
* URLs that are fetched and then the crawler starts following links
* which are found in these pages */
        controller.addSeed("https://www.bostonglobe.com/");
/* Start the crawl. This is a blocking operation, meaning that your code
* will reach the line after this only when crawling is finished. */
        controller.start(MyCrawler.class, numberOfCrawlers);
       /* File dontfetch=new File("C:/Users/Renuka/Desktop/hw2/Ignore.txt");
        File fetch_site=new File("C:/Users/Renuka/Desktop/hw2/FetchBoston.csv");
        File visit_site=new File("C:/Users/Renuka/Desktop/hw2/VisitBoston.csv");
        File urls_site=new File("C:/Users/Renuka/Desktop/hw2/UrlsBoston.csv");
        FileReader reader=new FileReader("C:/Users/Renuka/Desktop/hw2/FetchBoston.csv");*/
    }
}
