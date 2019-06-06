package UserLabel.SMGUserLabel

import org.ansj.splitWord.analysis.ToAnalysis
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.feature.{HashingTF, IDF, StopWordsRemover, Tokenizer}
import org.apache.spark.ml.param.ParamMap
import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.sql.types.{IntegerType, LongType, StringType, StructField, StructType}

object UserLableGenerate {
  def main(args: Array[String]): Unit = {
    val spark=SparkSession.builder().master("local").appName("User-Label").getOrCreate()
    val sc=spark.sparkContext
    sc.setLogLevel("WARN")

//    val rdd=sc.parallelize(SMGData.getData.toArray())
//    val msgRDD=rdd.map(f=>{
//      val msg= f.toString.split("//w")
//      val mobile=msg(0)
//      val content=msg(1)
//      Row(mobile,content)
//    })
    val wordsRdd=sc.parallelize(SMGData.getData.toArray())
    val msgRDD=wordsRdd.map(f=>{
      val msgs=f.toString.split(":")
      Row(
        msgs(0).toLong,
        msgs(1)
      )
    })
    val schema=StructType(
      StructField("Mobile",LongType) ::
      StructField("Content",StringType) ::
      Nil)
    val msgDF=spark.createDataFrame(msgRDD,schema).toDF("msg","content")
    msgDF.printSchema()

    //管道
    val pipeLine=new Pipeline()
    //分词器
    val tokenizer=new Tokenizer().setInputCol("content").setOutputCol("msgWords")
    tokenizer.transform(msgDF).show(false)

    //停用词转换器
    val stopWordRemover=new StopWordsRemover().setInputCol("msgWords").setOutputCol("filteredWords")
    StopWordsRemover.loadDefaultStopWords("chinese")


    //TH词频率计数
    val hashTH=new HashingTF().setInputCol("filteredWords").setOutputCol("msgFeatures").setNumFeatures(10)

    //IDF文档逆词频统计
    val idf=new IDF().setInputCol("msgFeatures").setOutputCol("tf-idf-features")

    //配置管道
    pipeLine.setStages(Array(tokenizer,stopWordRemover,hashTH,idf))

    val pipelineModel=pipeLine.fit(msgDF)

    pipelineModel.transform(msgDF).show(false)
  }
}
