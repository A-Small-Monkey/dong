package MLlib.FeaturesScaler

import java.util

import org.apache.spark.ml.attribute.{Attribute, AttributeGroup, NumericAttribute}
import org.apache.spark.ml.feature.VectorSlicer
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{Row, SparkSession}

/**
  * 根据自己的选择过滤特征
  * setIndices 根据特征的下标索引过滤，setNames 需要提前为特征赋值AttributeGroup,然后根据Attribute过滤需要的特征
  * (Transformer)
  */
object VectorSlicerTest {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder().appName("VectorIndexer").master("local").getOrCreate()
    val sc = spark.sparkContext
    sc.setLogLevel("error")
    val data = util.Arrays.asList(Row(Vectors.dense(-2.0, 2.3, 0.0)))

    val defaultAttr = NumericAttribute.defaultAttr
    val attrs = Array("f1", "f2", "f3").map(defaultAttr.withName)
    val attrGroup = new AttributeGroup("userFeatures", attrs.asInstanceOf[Array[Attribute]])

    val dataset = spark.createDataFrame(data, StructType(Array(attrGroup.toStructField())))

    val slicer = new VectorSlicer().setInputCol("userFeatures").setOutputCol("features")

    //设置整数索引列，
    slicer.setIndices(Array(0)).setNames(Array("f3"))
//    slicer.setIndices(Array(1, 2))
//    slicer.setNames(Array("f2", "f3"))

    val output = slicer.transform(dataset)
    output.show(false)
    println(output.select("userFeatures", "features").first())
  }
}
