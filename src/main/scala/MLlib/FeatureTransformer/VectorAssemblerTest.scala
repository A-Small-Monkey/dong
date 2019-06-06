package MLlib.FeatureTransformer

import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.sql.types.{DoubleType, StructField, StructType}
import org.apache.spark.sql.{Row, SparkSession}

/**
  * VectorAssembler是一个Transformer，将多个列值放入一个数组，转为一个特征向量
  */
object VectorAssemblerTest {
  def main(args: Array[String]): Unit = {
    val spark=SparkSession.builder().master("local").appName("VectorAssembler").getOrCreate()

//    val dataDF=spark.createDataFrame(Array(
//      ("","","","","",1.0),
//      ("","","","","",1.0),
//      ("","","","","",1.0),
//      ("","","","","",1.0),
//      ("","","","","",1.0)
//    ))
    val sc=spark.sparkContext

    val data=sc.textFile("data/RandomForestData/data_banknote_authentication.txt").map(f=>{
      val values=f.split(",")
      val col1=values(0).toDouble
      val col2=values(1).toDouble
      val col3=values(2).toDouble
      val col4=values(3).toDouble
      val label=values(4).toDouble
      Row(col1,col2,col3,col4,label)
    })
    val scheme=StructType(
      StructField("col1",DoubleType,false)::
      StructField("col2",DoubleType,false)::
      StructField("col3",DoubleType,false)::
      StructField("col4",DoubleType,false)::
      StructField("col5",DoubleType,false)
        ::Nil)
    val dataDF=spark.createDataFrame(data,scheme).toDF("col1","col2","col3","col4","label")
    dataDF.printSchema()
    dataDF.show(20,false)

    val vectorAssembler= new VectorAssembler().setInputCols(Array("col1","col2","col3","col4")).setOutputCol("feature")

    vectorAssembler.transform(dataDF).show(100,false)

  }
}
