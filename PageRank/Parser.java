import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.*;
import java.util.HashMap;
import java.util.HashSet;

public class Parser {
    public static void main(String[] args) {
        System.out.println();
        File home=new File("F:/BG/BG/BG");
        File htmls[]=home.listFiles();
        HashMap<String,String> fileurlMap=new HashMap<>();
        HashMap<String,String> urlfileMap=new HashMap<>();
        HashSet<String> urlLinks=new HashSet<>();
        File csvmap= null,edgeList=null;
        try {
            csvmap = new File("F:/BG/BG/Boston Global Map.csv");
            BufferedReader bf=new BufferedReader(new FileReader(csvmap));
            String line;
            while((line=bf.readLine())!=null){
                String[] keyval=line.split(",");
                //System.out.println(keyval[0]+" "+keyval[1]);
                fileurlMap.put(keyval[0],keyval[1]);
                urlfileMap.put(keyval[1],keyval[0]);
            }
            int c=1;
            for(File html:htmls){
                String htmlfile=html.getName().toString();
                //System.out.println(htmlfile+" "+fileurlMap.get(htmlfile));
                Document doc=Jsoup.parse(html,"UTF-8", fileurlMap.get(htmlfile));
                Elements outlinks=doc.select("a[href]");
                for(Element ele : outlinks) {
                    String outlink=ele.attr("abs:href");
                    //System.out.println(outlink);
                    if(urlfileMap.containsKey(outlink)) {
                        urlLinks.add(htmlfile + " " + urlfileMap.get(outlink));
                    }
                    //System.out.println(ele.attr("abs:href"));
                }
            }
            System.out.println(urlLinks.size());
            edgeList = new File("F:/BG/BG/EdgeList.txt");
            BufferedWriter outFile=new BufferedWriter(new FileWriter(edgeList));

            for(String key:urlLinks){
                outFile.write(key);
                outFile.newLine();
            }
            bf.close();
            outFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
