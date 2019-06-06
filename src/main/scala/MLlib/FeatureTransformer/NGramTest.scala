package MLlib.FeatureTransformer

import org.apache.spark.ml.feature.NGram
import org.apache.spark.sql.SparkSession

/**
  * 对一个单词的序列从每个单词开始往后进行组合，每个单词和他后面的n-1个单词组合，如果组合的单词到了结尾则结束。
  */
object NGramTest{
  def main(args: Array[String]): Unit = {
    val spark=SparkSession.builder().appName("NGram").master("local").getOrCreate()
    val sc=spark.sparkContext
    sc.setLogLevel("error")
    val wordDataFrame = spark.createDataFrame(Seq(
      (0, Array("Hi", "I", "heard", "about", "Spark")),
      (1, Array("I", "wish", "Java", "could", "use", "case", "classes")),
      (2, Array("Logistic", "regression", "models", "are", "neat"))
    )).toDF("label", "words")

    val ngram = new NGram().setInputCol("words").setOutputCol("ngrams").setN(3)
    val ngramDataFrame = ngram.transform(wordDataFrame)
    ngramDataFrame.show(false)
    ngramDataFrame.take(3).map(_.getAs[Stream[String]]("ngrams").toList).foreach(println)
  }

}
