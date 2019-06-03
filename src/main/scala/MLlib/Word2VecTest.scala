package MLlib

import org.apache.spark.ml.feature.Word2Vec
import org.apache.spark.ml.linalg.Vector
import org.apache.spark.sql.{Row, SparkSession}

object Word2VecTest {
  val spark=SparkSession.builder().appName("MyTokenizerTest").master("local").getOrCreate()

  def main(args: Array[String]): Unit = {
    //test
    test1()
  }
  def test1(): Unit ={
    // Input data: Each row is a bag of words from a sentence or document.
    val documentDF = spark.createDataFrame(Seq(
      "Hi I heard about Spark".split(" "),
      "I wish Java could use case classes".split(" "),
      "Logistic regression models are neat".split(" ")
    ).map(Tuple1.apply)).toDF("text")

    // Learn a mapping from words to Vectors.
    val word2Vec = new Word2Vec()
      .setInputCol("text")
      .setOutputCol("result")
      .setVectorSize(4)
      .setMinCount(0)

    val model = word2Vec.fit(documentDF)

    val result = model.transform(documentDF)

    result.printSchema()

    result.collect().foreach { case Row(text: Seq[_], features: Vector) =>
      println(s"Text: [${text.mkString(",")}] => \nVector: $features\n") }
  }
}
