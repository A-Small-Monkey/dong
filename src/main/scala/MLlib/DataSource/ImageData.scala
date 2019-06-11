package MLlib.DataSource

import org.apache.spark.sql.SparkSession

object ImageData {

  def main(args: Array[String]): Unit = {
    val spark=SparkSession.builder().master("local").appName("image-data-test").getOrCreate()

    val imageDF=spark.read.format("image").option("dropInvalid",true).load("data/image")

    imageDF.show(false)
  }
}
