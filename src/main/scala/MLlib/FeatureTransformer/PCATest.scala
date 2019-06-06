package MLlib.FeatureTransformer

import org.apache.spark.ml.feature.PCA
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.sql.SparkSession

/**
  * PCA=> 主成分分析 特征向量的降维，将维向量降到3维向量，适用于图像处理
  */
object PCATest {

  def main(args: Array[String]): Unit = {

    val spark=SparkSession.builder().master("local").appName("PCA").getOrCreate()
    val data = Array(
      Vectors.sparse(5, Seq((1, 1.0), (3, 7.0))),
      Vectors.dense(2.0, 0.0, 3.0, 4.0, 5.0),
      Vectors.dense(4.0, 0.0, 0.0, 6.0, 7.0)
    )
    val df = spark.createDataFrame(data.map(Tuple1.apply)).toDF("features")
    val pca = new PCA()
      .setInputCol("features")
      .setOutputCol("pcaFeatures")
      .setK(3)
      .fit(df)
    val pcaDF = pca.transform(df)
    pcaDF.show(false)
    val result = pcaDF.select("pcaFeatures")
  }
}
