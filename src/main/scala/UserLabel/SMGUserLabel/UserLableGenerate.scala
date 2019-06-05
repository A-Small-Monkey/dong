package UserLabel.SMGUserLabel

import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.feature.{HashingTF, Tokenizer}
import org.apache.spark.ml.param.ParamMap
import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.sql.types.{IntegerType, LongType, StringType, StructField, StructType}

object UserLableGenerate {
  def main(args: Array[String]): Unit = {
    val spark=SparkSession.builder().master("local").appName("User-Label").getOrCreate()
    val sc=spark.sparkContext
    sc.setLogLevel("WARN")

    val rdd=sc.parallelize(SMGData.getData.toArray())
    val msgRDD=rdd.map(f=>{
      val msg=f.toString.split(":")
      val mobile=msg(0).toLong
      val content=msg(1)
      Row(mobile,content)
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
    tokenizer.transform(msgDF).show()

    //TH词频率计数
    val hashTH=new HashingTF().setInputCol("msgWords").setOutputCol("msgFeatures").setNumFeatures(10)

    //配置管道
    pipeLine.setStages(Array(tokenizer,hashTH))

    val pipelineModel=pipeLine.fit(msgDF)

    pipelineModel.transform(msgDF).select("msg","msgWords","")
  }
}
