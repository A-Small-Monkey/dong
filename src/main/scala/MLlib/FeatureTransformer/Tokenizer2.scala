package MLlib.FeatureTransformer

import org.apache.spark.ml.feature.{RegexTokenizer, Tokenizer}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.{col, udf}

/**
  *
  * Tokenizer分词器只能将文本按照空格切开，非空格切割词汇是一个单词,并且单词全都转换为小写
  */
object Tokenizer2 {
  def main(args: Array[String]): Unit = {
    //test
    test()
  }
  def test(): Unit ={
    val spark=SparkSession.builder().appName("MyTokenizerTest").master("local").getOrCreate()

    val sentenceDataFrame = spark.createDataFrame(Seq(
      (0, "Hi I heard about Spark 王栋"),
      (1, "I wish Java could use case classes 机器"),
      (2, "Logistic,regression,models,are,neat 学习")
    )).toDF("id", "sentence")

    val tokenizer = new Tokenizer().setInputCol("sentence").setOutputCol("words")
    val regexTokenizer = new RegexTokenizer()
      .setInputCol("sentence")
      .setOutputCol("words")
      .setPattern("\\w")
    // \\W匹配非字符，非字符会被过滤，\\w匹配字符，数字类型被过滤

    val countTokens = udf { (words: Seq[String]) => words.length }

    val tokenized = tokenizer.transform(sentenceDataFrame)
    tokenized.select("sentence", "words")
      .withColumn("tokens", countTokens(col("words"))).show(false)

    val regexTokenized = regexTokenizer.transform(sentenceDataFrame)
    regexTokenized.select("sentence", "words")
      .withColumn("tokens", countTokens(col("words"))).show(false)
    regexTokenizer.transform(sentenceDataFrame) .withColumn("tokens", countTokens(col("words"))).show(false)
  }
}
