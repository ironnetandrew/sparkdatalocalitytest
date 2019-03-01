package com.awcoleman.sparkdatalocalitytest;

import org.apache.hadoop.mapred.InputSplit;
import org.apache.log4j.Logger;
import org.apache.spark.Partition;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.rdd.HadoopPartition;
import org.apache.spark.rdd.MapPartitionsRDD;


import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

public class MapRDataLocalityTest {

    private static Logger logger = Logger.getLogger( MethodHandles.lookup().lookupClass() );

    public MapRDataLocalityTest(String inputPath) {

        SparkConf conf = new SparkConf().setAppName(this.getClass().getName() );
        JavaSparkContext sc = new JavaSparkContext(conf);
        //String path = "linescount.txt";

        logger.info("Trying to open: " + inputPath);

        JavaRDD<String> linesrdd = sc.textFile(inputPath);

        List<Partition> linesPartitions = linesrdd.partitions();

        for (Partition linesPart : linesPartitions) {
            logger.info("Examining Partition: " + linesPart +" with index "+
                    linesPart.index() +". Will output preferredLocations (if any) next.");
            scala.collection.Seq<String> linesPreferrefLocationsSeq = linesrdd.rdd().preferredLocations(linesPart);
            if (linesPreferrefLocationsSeq.isEmpty()) {
                logger.info("Preferred Locations is empty for Partition"+ linesPart);
            } else {
                List<String> linesPreferrefLocations = scala.collection.JavaConversions.seqAsJavaList(linesPreferrefLocationsSeq);
                for (String prefLoc : linesPreferrefLocations) {
                    logger.info("Preferred Location: " + prefLoc);
                }
            }
        }

        for (Partition linesPart : linesPartitions) {
            logger.info("Examining Partition: " + linesPart +" with index "+
                    linesPart.index() +". Will output preferredLocations (if any) next.");
            HadoopPartition hpart = null;
            try {
                hpart = (HadoopPartition) linesPart;
            } catch (ClassCastException e) {
                e.printStackTrace();
                System.exit(1);
            }
            InputSplit split = hpart.inputSplit().value();
            logger.info("Input split: " + split);
            try {
                logger.info("InputSplit getLength: " + split.getLength());
                //logger.info("InputSplit getLocationInfo: " + split.getLocationInfo());
                for (String loc : split.getLocations()) {
                    logger.info("InputSplit getLocations entry: " + loc);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



        logger.info("Lines count: " + linesrdd.count());
        sc.stop();
    }

    public static void main(String[] args) {

        if (args.length < 1 ) {
            System.out.println("Missing an argument. Exiting.");
            logger.fatal("Missing an argument. Exiting.");
            System.exit(1);
        }
        //System.out.println("Argument is "+args[0]);

        MapRDataLocalityTest mainobj = new MapRDataLocalityTest(args[0]);
    }


}
