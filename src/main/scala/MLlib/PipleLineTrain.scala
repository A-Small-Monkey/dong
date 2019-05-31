package MLlib

import org.apache.spark.ml.{Pipeline, PipelineModel}
import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.ml.feature.{HashingTF, Tokenizer}
import org.apache.spark.ml.param.ParamMap
import org.apache.spark.sql.SparkSession

object PipelineTrain {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().appName("name").master("local[*]").getOrCreate()
    //创建数据集
    val training = spark.createDataFrame(Seq(
      (0L, "a b c d e spark", 1.0),
      (1L, "b d", 0.0),
      (2L, "spark f g h", 1.0),
      (3L, "hadoop mapreduce", 0.0)
    )).toDF("id", "text", "label")

    //创建转化器
    val tokenizer = new Tokenizer().setInputCol("text").setOutputCol("words")
    val hashingTF = new HashingTF().setInputCol(tokenizer.getOutputCol)
    //创建估计器
    val lr = new LogisticRegression()
    //创建参数Map
    val paramMap = ParamMap(hashingTF.outputCol -> "features", hashingTF.numFeatures -> 1000,lr.maxIter -> 10, lr.regParam -> 0.01)
    //创建管道，并传入转化器、估计器
    val pipeline = new Pipeline()
    pipeline.setStages(Array(tokenizer, hashingTF, lr))
    //
    val model = pipeline.fit(training, paramMap)
    //存储模型、pipeline
    model.write.overwrite().save("")
    pipeline.write.overwrite().save("")
    //读取模型、pipeline
    val sameModel_load=PipelineModel.load("")
    val pipeline_load=Pipeline.load("path")
    //预测数据
    model.transform(training).show()
  }
}
