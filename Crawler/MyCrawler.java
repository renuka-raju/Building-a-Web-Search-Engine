package crawler;

import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.Set;
public class MyCrawler extends WebCrawler {
    private final static Pattern FILTERS =
            Pattern.compile(".*(\\.(css|js|"
            + "mid|mp2|mp3|mp4|json"
            + "|wav|avi|mov|mpeg|ram|m4v|pdf"
            + "|rm|smil|wmv|swf|wma" +
                    "zip|rar|gz)).*$");
    /**
     * This method receives two parameters. The first parameter is the page
     * in which we have discovered this new url and the second parameter is
     * the new url. You should implement this function to specify whether
     * the given url should be crawled or not (based on your crawling logic).
     * In this example, we are instructing the crawler to ignore urls that
     * have css, js, git, ... extensions and to only accept urls that start
     * with "http://www.cnn.com/". In this case, we didn't need the
     * referring Page parameter to make the decision.
     */
    File dontfetch=new File("C:/Users/Renuka/Desktop/hw2/Ignore.txt");
    File fetch_site=new File("C:/Users/Renuka/Desktop/hw2/FetchBoston.csv");
    File visit_site=new File("C:/Users/Renuka/Desktop/hw2/VisitBoston.csv");
    File urls_site=new File("C:/Users/Renuka/Desktop/hw2/UrlsBoston.csv");
    private static final String NEWSITE="https://www.bostonglobe.com/";
    int chtml=0;
    int cothers=0;
    private static int countall=0,countunique=0,countinnews=0,countoutnews=0;
    private static final String COMMA = ",";
    private static final String NEWLINE = "\n";
    private static Set<String> allunique=new HashSet<String>();
    private static Set<String> newsunique=new HashSet<String>();
    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        int question=href.indexOf("?");
        if(question!=-1)
            href=href.substring(0,question);
        countall++;
        allunique.add(href);
        String place;
        try {
            FileWriter writer = new FileWriter(dontfetch.getAbsolutePath(),true);
            FileWriter urlswrite=new FileWriter(urls_site.getAbsolutePath(),true);
            if (FILTERS.matcher(href).matches()
                    || !href.startsWith(NEWSITE)){
                writer.write(href);
                writer.flush();
                writer.close();
            }

            if(href.startsWith(NEWSITE)){
                newsunique.add(href);
                place="OK";
            }
            else {
                place = "N_OK";
            }
            urlswrite.write(href);
            urlswrite.write(COMMA);
            urlswrite.write(place);
            urlswrite.write(NEWLINE);
            urlswrite.flush();
            //urlswrite.close();
        }
        catch (Exception e){
            System.out.println(e.getStackTrace());
        }
        return !FILTERS.matcher(href).matches()
                && href.startsWith(NEWSITE);
    }

    /**
     * This function is called when a page is fetched and ready
     * to be processed by your program.
     */
int csuccess=0,cfailabort=0,cattempt=0;
//int redirect=0,fail=0,abort=0;
Map<Integer,Integer> codeset=new HashMap<Integer,Integer>();
Map<String,Integer> sizemap=new HashMap<String,Integer>();
Map<String,Integer> typemap=new HashMap<String,Integer>();
    @Override
    public void onStart(){
        sizemap.put("<KB",0);
        sizemap.put("<10KB",0);
        sizemap.put("<100KB",0);
        sizemap.put("<1000KB",0);
        sizemap.put("<1MB",0);
    }
    @Override
    public void visit(Page page) {
        cattempt++;
        String ref = page.getWebURL().getURL();
        String url = ref.replaceAll(",","-");
        String type=page.getContentType();
        int statuscode=page.getStatusCode();
        int pagesize=page.getContentData().length;
        Set<WebURL> links=new HashSet<WebURL>();
        if(type.contains("html")){
            System.out.println("html type is :"+type);
            chtml++;
        }
        else{
            System.out.println("other type is :"+type);
            cothers++;
        }
        if(!codeset.containsKey(statuscode)){
            codeset.put(statuscode,1);
        }
        else{
            codeset.put(statuscode,codeset.get(statuscode)+1);
        }
        if(!typemap.containsKey(type)){
            typemap.put(type,0);
        }
        else{
            typemap.put(type,typemap.get(type)+1);
        }
        if(statuscode>=200 && statuscode<300)
            csuccess++;
        if(statuscode>=300 && statuscode<600)
            cfailabort++;

        if(pagesize<1024){
            sizemap.put("<KB",sizemap.get("<KB")+1);
        }
        else if(pagesize>=1024&&pagesize<10240){
            sizemap.put("<10KB",sizemap.get("<10KB")+1);
        }
        else if(pagesize>=10240&&pagesize<102400){
            sizemap.put("<100KB",sizemap.get("<100KB")+1);
        }
        else if(pagesize>=102400&&pagesize<1024000){
            sizemap.put("<1000KB",sizemap.get("<1000KB")+1);
        }
        else{
            sizemap.put("<1MB",sizemap.get("<1MB")+1);
        }

        System.out.println("URL: " + url);
        if (page.getParseData() instanceof HtmlParseData) {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            String text = htmlParseData.getText();
            String html = htmlParseData.getHtml();

            links = htmlParseData.getOutgoingUrls();
            System.out.println("Text length: " + text.length());
            System.out.println("Html length: " + html.length());
            System.out.println("Number of outgoing links: " + links.size());
        }
        FileWriter fetchwrite, visitwrite;
        try{
            fetchwrite=new FileWriter(fetch_site.getAbsolutePath(),true);
            fetchwrite.write(url);
            fetchwrite.write(COMMA);
            fetchwrite.write(String.valueOf(statuscode));
            fetchwrite.write(NEWLINE);
            fetchwrite.flush();
            if(statuscode>=200 && statuscode<300) {
                visitwrite = new FileWriter(visit_site.getAbsolutePath(), true);
                visitwrite.write(url);
                visitwrite.write(COMMA);
                visitwrite.write(String.valueOf(pagesize));
                visitwrite.write(COMMA);
                visitwrite.write(String.valueOf(links.size()));
                visitwrite.write(COMMA);
                visitwrite.write(type);
                visitwrite.write(NEWLINE);
                visitwrite.flush();
            }
        }
        catch(IOException ioe){

        }
    }
    @Override
    public synchronized void onBeforeExit() {
        System.out.println("grand total :"+countall);
        System.out.println("unique fetched :"+allunique.size());
        System.out.println("unique news fetched :"+newsunique.size());
        System.out.println("unique outside news :"+(allunique.size()-newsunique.size()));
        System.out.println("no of html : "+chtml);
        System.out.println("no of others: "+cothers);
        System.out.println("no of attempts : "+cattempt);
        System.out.println("no of failedabort : "+cfailabort);
        System.out.println("no of succeeded : "+csuccess);
        System.out.println("no of succeeded : "+csuccess);
        System.out.println("Distinct status code");
        for (Integer entry:codeset.keySet()){
            System.out.println(entry+" "+codeset.get(entry));
        }
        System.out.println("Distinct file sizes");
        for (String entry:sizemap.keySet()){
            System.out.println(entry+" "+sizemap.get(entry));
        }
        System.out.println("Distinct content types");
        for (String entry:typemap.keySet()){
            System.out.println(entry+" "+typemap.get(entry));
        }
    }
}
