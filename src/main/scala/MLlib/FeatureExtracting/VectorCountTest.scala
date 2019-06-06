package MLlib.FeatureExtracting

import org.apache.spark.sql.SparkSession

object VectorCountTest {
  def main(args: Array[String]): Unit = {
    val spark=SparkSession.builder().master("local").appName("countVector").getOrCreate()
    val sc=spark.sparkContext
    sc.setLogLevel("error")

//    val dataDF=spark.createDataFrame(Array()).toDF("","")
  }
}
