package MLlib.FeatureTransformer

import org.apache.spark.SparkConf
import org.apache.spark.ml.feature.QuantileDiscretizer
import org.apache.spark.sql.SparkSession

/**
  * 连续离散型变量的分桶。
  */
object QuantileDiscretizerExample {

  def main(args: Array[String]) {
    val sparkConf = new SparkConf()
    sparkConf.setMaster("local[*]").setAppName(this.getClass.getSimpleName)
    val spark = SparkSession
      .builder
      .config(sparkConf)
      .appName("QuantileDiscretizerExample")
      .getOrCreate()

    val data = Array((0, 18.0), (1, 19.0), (2, 8.0), (3, 5.0), (4, 2.2))
    val df = spark.createDataFrame(data).toDF("id", "hour").repartition(1)

    val discretizer = new QuantileDiscretizer()
      .setInputCol("hour")
      .setOutputCol("result")
      .setNumBuckets(3)

    val result = discretizer.fit(df).transform(df)
    result.show(false)

    spark.stop()
  }
}
