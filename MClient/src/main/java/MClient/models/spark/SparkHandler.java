package MClient.models.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Ben on 13/05/2016.
 */
public class SparkHandler {


    public static String process(ClientWithSparkInstruction clientWithSparkInstruction){

        String filePath = clientWithSparkInstruction.getSingleClientSparkInstruction().getFileDirectory();
        SparkType type = clientWithSparkInstruction.getSingleClientSparkInstruction().getSparkType();
        boolean sorted = clientWithSparkInstruction.getSingleClientSparkInstruction().isSorted();

        //d:\Ben\Desktop\test-small.txt

        //String t = "d:\\Ben\\Desktop\\test-small.txt";
        String t2 = "d:/Ben/Desktop/test-small.txt";
       // d:\Ben\Desktop


        if(validateFile(t2)){
            if(type == SparkType.WORDCOUNT){
                return wordCount(t2, sorted);
            }
            else if(type == SparkType.WORDSEARCH){
                wordSearch(t2, sorted);
            }else{

            }
        }else{
            return "The file could not be found or could not be read at directory: " + filePath;
        }














        return "";
    }

    private static String wordCount(String filePath, boolean sorted){
        String log = ""; //used incase of exception
        try{

            // Define a configuration to use to interact with Spark
            System.out.println("==========================PHASE 1 ================");

            SparkConf conf = new SparkConf().setMaster("local").setAppName("Work Count App");
            log += "Phase one complete\n";

//
            System.out.println("==========================PHASE 2 ================");
//        // Create a Java version of the Spark Context from the configuration
            JavaSparkContext sc = new JavaSparkContext(conf);
            log += "Phase two complete\n";
//
            System.out.println("==========================PHASE 3 ================");
//        // Load the input data, which is a text file read from the command line
            JavaRDD<String> input = sc.textFile( filePath );
            log += "Phase three complete\n";
//
            System.out.println("==========================PHASE 4 ================");
//        // Java 7 and earlier
            JavaRDD<String> words = input.flatMap(
                    new FlatMapFunction<String, String>() {
                        public Iterable<String> call(String s) {
                            return Arrays.asList(s.split(" "));
                        }
                    } );
//
            log += "Phase four complete\n";

            System.out.println("==========================PHASE 5 ================");
//        // Java 7 and earlier: transform the collection of words into pairs (word and 1)
            JavaPairRDD<String, Integer> counts = words.mapToPair(
                    new PairFunction<String, String, Integer>(){
                        public Tuple2<String, Integer> call(String s){
                            return new Tuple2(s, 1);
                        }
                    } );
//
            log += "Phase five complete\n";

            System.out.println("==========================PHASE 6 ================");
//        // Java 7 and earlier: count the words
            JavaPairRDD<String, Integer> reducedCounts = counts.reduceByKey(
                    new Function2<Integer, Integer, Integer>(){
                        public Integer call(Integer x, Integer y){ return x + y; }
                    } );

            log += "Phase six complete\n";
//
            System.out.println("==========================PHASE 7 ================");
            List arr = reducedCounts.collect();

            log += "Phase seven complete\n";
//        // Save the word count back out to a text file, causing evaluation.
//
//
            for(Object a : arr){
                Tuple2<String, Integer> tuple = (Tuple2)a;
                System.out.println(tuple);
            }

            return "done";

        }catch (Exception e){
            System.out.println("ERROR FROM WITHIN SPARK ");
            return log + "\n " + e.getStackTrace();
        }
    };


    private static void wordSearch(String filePath, boolean sorted){};

    private static boolean validateFile(String filePath){

        File file = new File(filePath);
        return file.exists() && file.canRead();
//        System.out.println(file.toString()); //d:\Ben\Desktop\test-small.txt
//        System.out.println(file.exists()); //bool
//        System.out.println(file.getParentFile()); //d:\Ben\Desktop
//        System.out.println(file.getAbsoluteFile()); //d:\Ben\Desktop\test-small.txt
    }
}
