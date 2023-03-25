import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;

import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.*;
import java.util.HashMap;

public class WordCount {
  public static class TokenizerMapper extends Mapper<Object, Text, Text, Text> {
   // private final static IntWritable one = new IntWritable(1);
    private Text word = new Text();

    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
     // System.out.print("\ncalled\n");
      //String line = value.toString();
      String linetest = value.toString();
      String[] tokens = linetest.split("\t",2);
      Text document_Id = new Text(tokens[0]);
      String secondline=tokens[1].replaceAll("[^A-Za-z]+", " ").toLowerCase();
      StringTokenizer itr = new StringTokenizer(secondline);
      //String prev = itr.nextToken();
      while (itr.hasMoreTokens()) {
        word.set(itr.nextToken());
        context.write(word, document_Id);
      }
    }
  }

  public static class IntSumReducer extends Reducer<Text, Text, Text, Text> {

    public void reduce(Text key, Iterable<Text> values, Context context)
        throws IOException, InterruptedException {
       HashMap<String, Integer> hashmap = new HashMap();
                         for(Text val: values)
                         {
                                String v = val.toString();
                                if(! hashmap.containsKey(v)){
                                         hashmap.put(v,  Integer.valueOf(1));
                                }
                                 else{
                                        int old_v = hashmap.get(v);
                                        int new_v = old_v+1;
                                        hashmap.put(v, Integer.valueOf(new_v));
                                 }
                         }
                         StringBuilder sbuilder = new StringBuilder("");
                         for(String docid: hashmap.keySet())
                         {
                                 sbuilder.append(docid+":"+hashmap.get(docid)+" ");
                         }
                         Text output_V = new Text(sbuilder.toString());
                         context.write(key, output_V);}
  }


  public static void main(String[] args) throws Exception {

  Configuration conf = new Configuration();
  Job job = Job.getInstance(conf, "inverted index");
  job.setJarByClass(WordCount.class);
  job.setMapperClass(TokenizerMapper.class);
  job.setReducerClass(IntSumReducer.class);
  job.setMapOutputKeyClass(Text.class);
  job.setMapOutputValueClass(Text.class);
  job.setOutputKeyClass(Text.class);
  job.setOutputValueClass(Text.class);
  FileInputFormat.addInputPath(job, new Path(args[0]));
  FileOutputFormat.setOutputPath(job, new Path(args[1]));
  System.exit(job.waitForCompletion(true)? 0 : 1);
  }
}// WordCount
