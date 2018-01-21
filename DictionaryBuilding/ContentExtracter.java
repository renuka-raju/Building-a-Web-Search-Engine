import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.sax.BodyContentHandler;
import java.io.*;

import org.xml.sax.SAXException;

public class ContentExtracter {

    public static void main(final String[] args) throws IOException,SAXException, TikaException {

        //detecting the file type

        File home=new File("C:/Users/Renuka/Desktop/Assignments/hw4/BG/BG/BG/");
        File htmls[]=home.listFiles();
        File bigfile = new File("C:/Users/Renuka/Desktop/Assignments/hw4/big.txt");
        BufferedWriter outFile=new BufferedWriter(new FileWriter(bigfile));
        System.out.println(htmls.length);
        //int c=0;
        for(File file:htmls) {
            BodyContentHandler handler = new BodyContentHandler(-1);
            Metadata metadata = new Metadata();
            ParseContext pcontext = new ParseContext();
            HtmlParser htmlparser = new HtmlParser();
            //ssSystem.out.println(++c);
            FileInputStream inputstream = new FileInputStream(new File(file.getAbsolutePath().toString()));
            htmlparser.parse(inputstream, handler, metadata,pcontext);
            String[] sent=handler.toString().split(" ");
            for (String s : sent) {
                if (s.trim() != null && !s.trim().isEmpty()) {
                    String[] words = s.split(" ");
                    for (String w : words) {
                        w = w.trim().replaceAll("[^A-Za-z0-9'-]+", "");
                        w = w.trim();
                        if (w != null && !w.isEmpty()) {
                            //System.out.println(++c + " " + w.trim());
                            outFile.write(w);
                            outFile.newLine();
                        }
                    }
                }
            }
            //outFile.write(handler.toString());
        }
        outFile.close();

    }
}