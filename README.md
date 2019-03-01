# sparkdatalocalitytest

Various notes

-------------------------------------------------------------------------


https://spark.apache.org/docs/2.4.0/api/java/org/apache/spark/scheduler/SplitInfo.html  


https://spark.apache.org/docs/2.4.0/api/java/org/apache/spark/SparkContext.html  

```
makeRDD(scala.collection.Seq<scala.Tuple2<T,scala.collection.Seq<String>>> seq, scala.reflect.ClassTag<T> evidence$3)
Distribute a local Scala collection to form an RDD, with one or more location preferences (hostnames of Spark nodes) for each object.
seq - list of tuples of data and location preferences (hostnames of Spark nodes)
public RDD<String> textFile(String path,int minPartitions)
Read a text file from HDFS, a local file system (available on all nodes), or any Hadoop-supported file system URI, and return it as an RDD of Strings.
```

https://github.com/apache/spark/blob/master/core/src/main/scala/org/apache/spark/SparkContext.scala  
```
def textFile(path: String,minPartitions: Int = defaultMinPartitions): RDD[String] = withScope {
    assertNotStopped()
    hadoopFile(path, classOf[TextInputFormat], classOf[LongWritable], classOf[Text],minPartitions).map(pair => pair._2.toString).setName(path)
  }

  def hadoopFile[K, V](
    path: String,


    // A Hadoop configuration can be about 10 KiB, which is pretty big, so broadcast it.
    val confBroadcast = broadcast(new SerializableConfiguration(hadoopConfiguration))
    val setInputPathsFunc = (jobConf: JobConf) => FileInputFormat.setInputPaths(jobConf, path)
    new HadoopRDD(
      this,
      confBroadcast,
      Some(setInputPathsFunc),
      inputFormatClass,
      keyClass,
      valueClass,
      minPartitions).setName(path)
  }
```
Path is in setInputPathsFunc  


https://github.com/apache/spark/blob/master/core/src/main/scala/org/apache/spark/rdd/HadoopRDD.scala  
```
private[spark] class HadoopPartition(rddId: Int, override val index: Int, s: InputSplit)
  extends Partition {
```



-------------------------------------------------------------------------

https://github.com/apache/spark/blob/master/streaming/src/main/scala/org/apache/spark/streaming/util/HdfsUtils.scala  
https://github.com/apache/spark/blob/master/streaming/src/main/scala/org/apache/spark/streaming/rdd/WriteAheadLogBackedBlockRDD.scala  

HadoopPartition is defined in HadoopRDD.scala  
https://github.com/apache/spark/blob/master/core/src/main/scala/org/apache/spark/rdd/HadoopRDD.scala  
```
  override def compute(theSplit: Partition, context: TaskContext): InterruptibleIterator[(K, V)] = {
    val iter = new NextIterator[(K, V)] {

      private val split = theSplit.asInstanceOf[HadoopPartition]
      logInfo("Input split: " + split.inputSplit)
```

https://hadoop.apache.org/docs/stable/api/index.html?org/apache/hadoop/mapred/InputSplit.html  
```
long	getLength()
	Get the total number of bytes in the data of the InputSplit.
String[]	getLocations()
	Get the list of hostnames where the input split is located.
```

https://hadoop.apache.org/docs/stable/api/index.html?org/apache/hadoop/mapreduce/InputSplit.html  
```
abstract long	getLength()
	Get the size of the split, so that the input splits can be sorted by size.
SplitLocationInfo[]	getLocationInfo()
	Gets info about which nodes the input split is stored on and how it is stored at each location.
abstract String[]	getLocations()
	Get the list of nodes by name where the data for the split would be local.
```

-------------------------------------------------------------------------

