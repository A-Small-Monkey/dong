package MLlib

import org.apache.spark.ml.feature.{HashingTF, IDF, Tokenizer}
import org.apache.spark.sql.SparkSession

class TokenizerTest {

}

/**
  * tokenizer+id-idf
  */
object TokenizerTest{
  def main(args: Array[String]): Unit = {
    val spark=SparkSession.builder().appName("MyTokenizerTest").master("local").getOrCreate()

    val sentenceData = spark.createDataFrame(Seq(
      (0.0, "Hi I heard about Spark"),
      (0.0, "I wish Java could use case classes"),
      (1.0, "Logistic regression models are neat")
    )).toDF("label", "sentence")

    val tokenizer = new Tokenizer().setInputCol("sentence").setOutputCol("words")
    val wordsData = tokenizer.transform(sentenceData)

    val hashingTF = new HashingTF()
      .setInputCol("words").setOutputCol("rawFeatures").setNumFeatures(20)

    val featurizedData = hashingTF.transform(wordsData)
    // alternatively, CountVectorizer can also be used to get term frequency vectors

    val idf = new IDF().setInputCol("rawFeatures").setOutputCol("features")
    val idfModel = idf.fit(featurizedData)

    val rescaledData = idfModel.transform(featurizedData)
    rescaledData.select("label", "features").show()

//    val df0=spark.createDataFrame(Seq(
//      (0, "Hi I heard about Spark"),
//      (1, "I wish Java could use case classes"),
//      (2, "Logistic,regression,models,are,neat"))).toDF("label","sentence")
//    df0.foreach(print(_))
//    val tokenizer=new Tokenizer().setInputCol("sentence").setOutputCol("words")
//
//    val countTokens = { (words: Seq[String]) => words.length }
//    val df1=tokenizer.transform(df0)
//    df1.foreach(print(_))

  }
}
