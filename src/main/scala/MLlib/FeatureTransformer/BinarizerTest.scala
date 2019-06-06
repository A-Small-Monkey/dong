package MLlib.FeatureTransformer

import org.apache.spark.ml.feature.Binarizer
import org.apache.spark.sql.SparkSession

//Binarizer 是一个二值转换器
object BinarizerTest {
  def main(args: Array[String]): Unit = {
    val spark=SparkSession.builder().master("local").appName("Binarizer").getOrCreate()

    val dataDF=spark.createDataFrame(Seq(
      (1.0,0.2),
      (1.0,0.6),
      (1.0,0.8)
    )).toDF("label","value")


    //二值转换器
    val binarizer=new Binarizer().setInputCol("value").setOutputCol("binarizer-value").setThreshold(0.5)
    //
    val binarizerDF=binarizer.transform(dataDF)
    println(binarizer.threshold)
    binarizerDF.show(false)

  }
}
