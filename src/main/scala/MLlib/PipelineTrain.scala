package MLlib

import org.apache.log4j.lf5.LogLevel
import org.apache.spark.ml.{Pipeline, PipelineModel}
import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.ml.feature.{HashingTF, StopWordsRemover, Tokenizer}
import org.apache.spark.ml.param.ParamMap
import org.apache.spark.sql.SparkSession

/**
  * 管道解释一系列的DF的转换和计算。
  * transform=>tokenizer,stopWords
  *
  */
object PipelineTrain {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().appName("name").master("local[*]").getOrCreate()
    val sc=spark.sparkContext
    sc.setLogLevel(LogLevel.ERROR.toString)
    //创建数据集
    val training = spark.createDataFrame(Seq(
      (0L, "Hi I heard about Spark", 1.0),
      (1L, "I wish Java could use case classes", 0.0),
      (2L, "Logistic regression models are neat", 1.0),
      (3L, "Logistic regression models are neat", 0.0)
    )).toDF("id", "text", "label")

    //分词转化器
    val tokenizer = new Tokenizer().setInputCol("text").setOutputCol("words")
    val tokenizerDF=tokenizer.transform(training)
    tokenizerDF.show()

    //停用词转化器
    val stopWordRemove=new StopWordsRemover().setInputCol("words").setOutputCol("filtered")
    val stopWordsDF=stopWordRemove.transform(tokenizerDF)
    stopWordsDF.show()

    //HashingTF
    val hashingTF = new HashingTF().setInputCol(stopWordRemove.getOutputCol)

    //创建估计器
    val lr = new LogisticRegression()
    //创建参数Map
    val paramMap = ParamMap(hashingTF.outputCol -> "features", hashingTF.numFeatures -> 1000,lr.maxIter -> 10, lr.regParam -> 0.01)
    //创建管道，并传入转化器、估计器
    val pipeline = new Pipeline()
    pipeline.setStages(Array(tokenizer,stopWordRemove,hashingTF, lr))
    //管道训练模型
    val model = pipeline.fit(training, paramMap)
    //存储模型、pipeline
    model.write.overwrite().save("test/model")
    pipeline.write.overwrite().save("test/pipeline")
    //读取模型、pipeline
    val sameModel_load=PipelineModel.load("test/model")
    val pipeline_load=Pipeline.load("test/pipeline")
    //预测数据
    model.transform(training).show()
  }
}
