import java.io.IOException;
import java.util.HashMap;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Mapper;

class CountMapper extends Mapper<LongWritable, Text, Text, Text>
{
        private Text word = new Text();
        private Text docID=new Text();
        public void map(LongWritable key, Text value, Context cxt)throws IOException, InterruptedException
        {
                        String[] fields= value.toString().split("\t");
                        String[] line=fields[1].split(" ");
                        docID.set(fields[0]);
                        for(int i=0;i<line.length;i++)
                        {
                                word.set(line[i]);
                                cxt.write(word,docID);
                        }
        }
}

class CountReducer extends Reducer<Text, Text, Text, Text>
{
        public void reduce(Text key, Iterable<Text> values, Context cxt)throws IOException, InterruptedException
        {
                        HashMap<String,Integer> docCount=new HashMap<String,Integer>();
                        for (Text value:values)
                        {
                                String doc=value.toString();
                                if(docCount.get(doc)==null){
                                docCount.put(doc,1);
                                }
                                else{
                                docCount.put(doc,(int)(docCount.get(doc))+1);
                                }
                        }
                        String idcount=docCount.toString();
                        idcount=idcount.substring(1,idcount.length()-1);
                        idcount=idcount.replaceAll("=",":");
                        idcount=idcount.replaceAll(",","");
                        cxt.write(key, new Text(idcount));
        }
}

public class InvIndex
{
        public static void main(String[] args)throws IOException, ClassNotFoundException, InterruptedException
        {
                if(args.length!=2){
                                System.err.println("Usage: Word count <input path> <output path>");
                                System.exit(-1);
                }
                Job job=new Job();
                job.setJarByClass(InvIndex.class);
                job.setJobName("Doc ID Mapper:");
                FileInputFormat.addInputPath(job, new Path(args[0]));
                FileOutputFormat.setOutputPath(job, new Path(args[1]));
                job.setMapperClass(CountMapper.class);
                job.setReducerClass(CountReducer.class);
                job.setOutputKeyClass(Text.class);
                job.setOutputValueClass(Text.class);
                job.waitForCompletion(true);
        }
}