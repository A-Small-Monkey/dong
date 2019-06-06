package MLlib.FeatureExtracting

import org.apache.log4j.lf5.LogLevel
import org.apache.spark.ml.feature.Word2Vec
import org.apache.spark.ml.linalg.Vector
import org.apache.spark.sql.{Row, SparkSession}

/**
  * Word2Vec 单词向量。
  * 1.由seq序列数据生成分词之后的数组流 documentDF
  * 2.设置word2Vec向量 输入col 输出col 输出向量大小，输出总数
  * 3.word2Vec fit文档，生成该documentDF的Word2VecModel
  * 4.documentDF文档对应的Word2VecModel transform 转换为最终输出结果的DF
  */
object Word2VecTest {
  val spark=SparkSession.builder().appName("MyTokenizerTest").master("local").getOrCreate()

  val sc=spark.sparkContext

  def main(args: Array[String]): Unit = {
    //test
    test1()
  }
  def test1(): Unit ={
    sc.setLogLevel(LogLevel.ERROR.toString)
    // Input data: Each row is a bag of words from a sentence or document.
    val sourceDF = spark.createDataFrame(Seq(
      "Hi I heard about Spark".split(" "),
      "I wish Java could use case classes".split(" "),
      "Logistic regression models are neat".split(" ")
    ).map(Tuple1.apply))

    sourceDF.printSchema()

    val documentDF=sourceDF.toDF("text")

    // Learn a mapping from words to Vectors.
    val word2Vec = new Word2Vec()
      .setInputCol("text")
      .setOutputCol("result")
      .setVectorSize(4)
      .setMinCount(0)

    //Vector类型的 model
    val model = word2Vec.fit(documentDF)

    //转换为(text,Vector）的向量DF
    val result = model.transform(documentDF)

    result.show(false)

    result.printSchema()

    result.collect().foreach { case Row(text: Seq[_], features: Vector) =>
      println(s"Text: [${text.mkString(",")}] => \nVector: $features\n") }
  }
}
