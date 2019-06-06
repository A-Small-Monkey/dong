package MLlib.FeatureTransformer

import org.apache.spark.ml.feature.Normalizer
import org.apache.spark.sql.SparkSession

/**
  * Normalizer正则转换器，
  */
object Normailzer {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().appName("VectorIndexer").master("local").getOrCreate()
    val sc = spark.sparkContext
    sc.setLogLevel("error")
    val dataFrame = spark.read.format("libsvm").load("data/testData/sample_libsvm_data.txt")

    val normalizer = new Normalizer()
      .setInputCol("features")
      .setOutputCol("normFeatures")
      .setP(1.0)

    val l1NormData = normalizer.transform(dataFrame)
    l1NormData.show(10,false)

    val lInfNormData = normalizer.transform(dataFrame, normalizer.p -> Double.PositiveInfinity)
    lInfNormData.show(10,false)
  }

}
