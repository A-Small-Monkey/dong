package MLlib.FeatureTransformer

import org.apache.spark.ml.feature.{IndexToString, StringIndexer}
import org.apache.spark.sql.SparkSession

/**
  * 使用StringIndexer将string类型的标签转换为Double的index
  * 最后对于预测出来的标签需要识别，所以需要将Double类型的标签使用IndexToString返回原始标签名
  * StringToIndexer，IndexToString都是一个Estimator
  */
object StringIndexerTest {
  def main(args: Array[String]): Unit = {
    val spark=SparkSession.builder().appName("StringIndexerEstimator").master("local").getOrCreate()

    val DataDF=spark.createDataFrame(Array(
      ("wd", "Hi I heard about Spark",1),
      ("zam", "I wish Java could use case classes",2),
      ("dong", "Logistic regression models are neat",3),
      ("man", "Logistic regression models are neat",4),
      ("man", "Logistic regression models are neat",5)
    )).toDF("label", "sentence","id")

    //val tokenizer=new Tokenizer().setInputCol("sentence").setOutputCol("words")

    //val tokenizerDF=tokenizer.transform(DataDF)
    //tokenizerDF.show(false)

    //对输入的标签列做一个数值转化
    val stringIndexer=new StringIndexer().setInputCol("label").setOutputCol("Indexer")

    //训练数值转换标签的Model
    val stringIndexerModel=stringIndexer.fit(DataDF)

    val stringIndexerDF=stringIndexerModel.transform(DataDF)

    stringIndexerDF.show(false)

    //将Index的标签转换为原始标签
    val indexerToString=new IndexToString().setInputCol("Indexer").setOutputCol("rawLabel").setLabels(stringIndexerModel.labels)

    indexerToString.transform(stringIndexerDF).show()

  }

}
