package MLlib.FeatureTransformer

import org.apache.spark.ml.feature.PolynomialExpansion
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.sql.SparkSession


/**
  * 进行多项式的展开。二维的特征向量执行，需要设置最高次幂setDegree()
  * 算法具体过程
  * 从第一维度开始，先计算1，2，3次幂，在计算
  * plo(x,y)=x,y,x2,x2+y2,x+y2,x3,y3,x+y,x2,y2
  * plo(2.0,2.3)=-2.0   -2.0^2=4.0   -2^3=-8.0    2.3    2.3^2   -2.0^2+2.3=        2.3*2.3-2.0=5.29   2.3^3=12.167   -2*2.3*2.3=-10.578
  */
object PolynomialExpansionTest {
  def main(args: Array[String]): Unit = {

    val spark=SparkSession.builder().appName("NGram").master("local").getOrCreate()
    val sc=spark.sparkContext
    sc.setLogLevel("error")

    val data = Array(
      Vectors.dense(-2.0, 2.3),
      Vectors.dense(0.0, 0.0),
      Vectors.dense(0.6, -1.1)
    )
    val df = spark.createDataFrame(data.map(Tuple1.apply)).toDF("features")
    val polynomialExpansion = new PolynomialExpansion()
      .setInputCol("features")
      .setOutputCol("polyFeatures")
      .setDegree(3)
    val polyDF = polynomialExpansion.transform(df)
    polyDF.show(false)
    /*val data = Array(
      Vectors.dense(1.0, 5.0),
      Vectors.dense(0.0, 0.0),
      Vectors.dense(3.0, -1.0)
    )
    val df = spark.createDataFrame(data.map(Tuple1.apply)).toDF("features")
    //setDegree表示多项式最高次幂 比如1.0,5.0可以是 三次：1.0^3 5.0^3 1.0+5.0^2 二次：1.0^2+5.0 1.0^2 5.0^2 1.0+5.0 一次：1.0 5.0
    val polyExpansion = new PolynomialExpansion()
      .setInputCol("features")
      .setOutputCol("polyFeatures")
      .setDegree(3)

    val polyDF = polyExpansion.transform(df)
    polyDF.show(false)*/


  }
}
