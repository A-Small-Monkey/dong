package MLlib.FeatureExtracting

import org.apache.spark.ml.feature.{HashingTF, IDF, StopWordsRemover, Tokenizer}
import org.apache.spark.sql.SparkSession

/**
  * tokenizer+id-idf
  * “词频－逆向文件频率”（TF-IDF）是一种在文本挖掘中广泛使用的特征向量化方法，
  *   它可以体现一个文档中词语在语料库中的重要程度。
  */

object TF_IDF{
  def main(args: Array[String]): Unit = {
    val spark=SparkSession.builder().appName("MyTokenizerTest").master("local").getOrCreate()

    val sentenceData = spark.createDataFrame(Seq(
      ("wd", "Hi I heard about Spark",1),
      ("zam", "I wish Java could use case classes",2),
      ("dong", "Logistic regression models are neat",3),
      ("man", "Logistic regression models are neat",4)
    )).toDF("label", "sentence","id")

    //分词转换
    val tokenizer = new Tokenizer().setInputCol("sentence").setOutputCol("words")
    val tokenizerDF=tokenizer.transform(sentenceData)

    //停用词转换
    val stopWordRemover=new StopWordsRemover().setInputCol("words").setOutputCol("filtered")
    val stopWordRemoveDF=stopWordRemover.transform(tokenizerDF)

    //词频转换，将一个词集合转换为一组向量，统计一个词在文档中出现的频率，向量所在维度的数字就是词在该文档中出现的次数
    //HashingTF是一个特征转换器，利用hash的当时将词转换为特征向量。
    //NumFeatures为做hash的分桶数
    val hashingTF = new HashingTF().setInputCol("filtered").setOutputCol("rawFeatures").setNumFeatures(20)
    val featurizedData = hashingTF.transform(stopWordRemoveDF)
    featurizedData.show(false)

    //IDF是一个Estimator 最终生成的IDFModel是一个 逆文档词频的Transformer
    val idf = new IDF().setInputCol("rawFeatures").setOutputCol("features")
    val idfModel = idf.fit(featurizedData)

    //对TF的特征向量进行转换，结果向量是每个词的重要程度，对应向量越小，说明词对文档的重要程度越高
    val rescaledData = idfModel.transform(featurizedData).show(false)
//    rescaledData.select("label", "features").show()

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
