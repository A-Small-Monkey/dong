package MLlib
import org.apache.spark.ml.feature.StopWordsRemover
import org.apache.spark.sql.SparkSession

object StopWordRemove {

  val spark=SparkSession.builder().appName("MyTokenizerTest").master("local").getOrCreate()

  def main(args: Array[String]): Unit = {
    val remover = new StopWordsRemover()
      .setInputCol("raw")
      .setOutputCol("filtered")

    val dataSet = spark.createDataFrame(Seq(
      (0, Seq("I", "saw", "the", "red", "balloon")),
      (1, Seq("Mary", "had", "a", "little", "lamb"))
    )).toDF("id", "raw")

    remover.transform(dataSet).show(true)
  }
}
