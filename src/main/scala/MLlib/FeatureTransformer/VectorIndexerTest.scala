package MLlib.FeatureTransformer

import org.apache.spark.ml.feature.VectorIndexer
import org.apache.spark.sql.SparkSession

/**
  * Estimator=>自动识别类别型向量，将类别型特征转换为索引特征
  * 对越特征列进行分类并为分类加索引
  * 一共有629t特征，分类之后变成326个特征，对于每一个特征内部分为0-4 5指标,指标可以为空
  */
object VectorIndexerTest {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().appName("VectorIndexer").master("local").getOrCreate()
    val sc = spark.sparkContext
    sc.setLogLevel("error")

    val dataDF = spark.read.format("libsvm").load("data/TestData/sample_libsvm_data.txt")

    val vectorIndexer=new VectorIndexer().setInputCol("features").setOutputCol("indexedFeatures").setMaxCategories(5)

    val viModel=vectorIndexer.fit(dataDF)

    viModel.transform(dataDF).show(100,false)
    println(viModel.numFeatures)  //692
    println(viModel.uid)  //vecIdx_db22542a8a37
    //分类特征数量：
    val categoricalFeatures: Set[Int] = viModel.categoryMaps.keys.toSet

    val features=viModel.categoryMaps
    for(i<- 0 to categoricalFeatures.size-1){
      println(i+"\t"+features.get(i))
    }

    println(s"Chose ${categoricalFeatures.size} categorical features: " +
      categoricalFeatures.mkString(", "))
  }


}
