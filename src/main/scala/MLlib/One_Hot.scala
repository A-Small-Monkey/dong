package MLlib

import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.feature.{OneHotEncoder, StringIndexer}
import org.apache.spark.sql.SparkSession

/**
  * 独热编码：OneHotEncoder 把标签列映射为二进制数组，适合一些期望类别特征为连续特征的算法
  */
object One_Hot {
  def main(args: Array[String]): Unit = {
    val spark=SparkSession.builder().master("local").appName("ont-hot").getOrCreate()

    val dataDF=spark.createDataFrame(Array(
      ("wd", "Hi I heard about Spark",1),
      ("zam", "I wish Java could use case classes",2),
      ("dong", "Logistic regression models are neat",3),
      ("man", "Logistic regression models are neat",4),
      ("man", "Logistic regression models are neat",5)
    )).toDF("label", "sentence","id")

    val stringIndex=new StringIndexer().setInputCol("label").setOutputCol("numLabel")
    val one_hot=new OneHotEncoder().setInputCol("numLabel").setOutputCol("hotLabel")

    val pipeline=new Pipeline()
    pipeline.setStages(Array(stringIndex,one_hot))
    val pipeLineModel=pipeline.fit(dataDF)

    pipeLineModel.transform(dataDF).show(false)

  }
}
