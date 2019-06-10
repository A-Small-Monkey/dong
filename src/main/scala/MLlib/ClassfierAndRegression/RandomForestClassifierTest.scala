package MLlib.ClassfierAndRegression

import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.RandomForestClassifier
import org.apache.spark.ml.feature.{IndexToString, StringIndexer, VectorAssembler}
import org.apache.spark.sql.types.{DoubleType, StructField, StructType}
import org.apache.spark.sql.{Row, SparkSession}

object RandomForestClassifierTest {

  def main(args: Array[String]): Unit = {

    val spark=SparkSession.builder().master("local").appName("DecisionTree").getOrCreate();
    val sc=spark.sparkContext
    sc.setLogLevel("error")

    val bike_hour_data=sc.textFile("data/TestData/hour.csv")

//    val arrayData=bike_hour_data.map(s=>{
//      val line=s.split(",")
//      val labelValue=line(line.length-1)
//      val featureValues=line.slice(2,line.length-3)
//      (labelValue,featureValues)
//    })
//
//    println(arrayData.first()._1)
//    arrayData.first()._2.foreach(println(_))
//    println()
//    val takeData=arrayData.map(f=>{
//
//      val features=Vectors.dense(f.slice(2,f.length-3).map(_.toDouble))
//
//      val label=f(f.length-1).toDouble
//
//      LabeledPoint
//      LabeledPoint(label,features)
//    })
//
//    takeData.foreach(println(_))


//    label   cnt
//    season  yr mnth hr holiday  weekday  workingday weathersit  temp  atemp   hum windspeed
    val dataDF=spark.read.option("inferSchema","true")
      //列分隔符
      .option("delimiter",",")
      //是否含有空值
      .option("nullValue", "?")
      //是否有表头
      .option("header", true)
      .csv("data/TestData/hour.csv")

    dataDF.printSchema()

//    dataDF.show(false)

    /**
      * instant|dteday             |season|yr |mnth|hr |holiday|weekday|workingday|weathersit|temp|atemp |hum |windspeed|casual|registered|cnt|
      * instant|dteday             |season|yr |mnth|hr |holiday|weekday|workingday|weathersit|temp|atemp |hum |windspeed|casual|registered|cnt|
      * +-------+-------------------+------+---+----+---+-------+-------+----------+----------+----+------+----+---------+------+----------+---+
      * |1      |2011-01-01 00:00:00|1     |0  |1   |0  |0      |6      |0         |1         |0.24|0.2879|0.81|0.0      |3     |13        |16 |
      * |0      |1                  |2     |3  |4   |5  |6      |7      |8         |9         |10  |11    |12  |13       |14     |15       |16 |
      *
      *
      */

//    val df=dataDF
//        .select("cnt","season","yr","mnth","hr","holiday","weekday","workingday","weathersit","temp","atemp","hum","windspeed","casual")

    val df1=dataDF.rdd.map(row=>{

      val labelValue=row.get(row.length-1).toString.toDouble

      val season=row.get(2).toString.toDouble
      val yr=row.get(3).toString.toDouble
      val mnth=row.get(4).toString.toDouble
      val hr=row.get(5).toString.toDouble
      val holiday=row.get(6).toString.toDouble
      val weekday=row.get(7).toString.toDouble
      val workingday=row.get(8).toString.toDouble
      val weathersit=row.get(9).toString.toDouble
      val temp=row.get(10).toString.toDouble
      val atemp=row.get(11).toString.toDouble
      val hum=row.get(12).toString.toDouble
      val windspeed=row.get(13).toString.toDouble
      val caual=row.get(14).toString.toDouble
//      var featureValue=Array(season,yr,mnth,hr,holiday,weekday,workingday,weathersit,temp,atemp,hum,windspeed,caual)

      Row(labelValue,season,yr,mnth,hr,holiday,weekday,workingday,weathersit,temp,atemp,hum,windspeed,caual)

    })

    val schema=StructType(
        StructField("lableValue",DoubleType)::
        StructField("season",DoubleType)::
        StructField("yr",DoubleType)::
        StructField("mnth",DoubleType)::
        StructField("hr",DoubleType)::
        StructField("holiday",DoubleType)::
        StructField("weekday",DoubleType)::
        StructField("workingday",DoubleType)::
        StructField("weathersit",DoubleType)::
        StructField("temp",DoubleType)::
        StructField("atemp",DoubleType)::
        StructField("hum",DoubleType)::
        StructField("windspeed",DoubleType)::
        StructField("caual",DoubleType)
        ::Nil
    )
    val df=spark.createDataFrame(df1,schema)
      .toDF("label","season","yr","mnth","hr","holiday","weekday","workingday","weathersit","temp","atemp","hum","windspeed","caual")

    val Array(train,test)=df.randomSplit(Array(0.8,0.2))


    val stringIndexer=new StringIndexer()
      .setInputCol("label")
      .setOutputCol("indexLabel")
      .fit(train)

    val vectorAssembler=new VectorAssembler()
      .setInputCols(Array("season","yr","mnth","hr","holiday","weekday","workingday","weathersit","temp","atemp","hum","windspeed","caual"))
      .setOutputCol("features")

//    val vi=new VectorIndexer()
//      .setInputCol("features")
//      .setOutputCol("indexFeatures")
//      .fit(train)

    val randomForest=new RandomForestClassifier()
      .setLabelCol("indexLabel")
      .setFeaturesCol("features")
      .setNumTrees(5)

    val indexToString=new IndexToString()
      .setInputCol("indexLabel")
      .setOutputCol("priLabel")
      .setLabels(stringIndexer.labels)

    val pipeline=new Pipeline().setStages(Array(stringIndexer,vectorAssembler,randomForest,indexToString))

    val model=pipeline.fit(train)

    model.transform(test).show()

    pipeline

//    val pipelineModel=pipeline.fit(train)
//
//    pipelineModel.transform(test).show(false)
//
//    pipelineModel.save("model/randomForestModel")
//
//    Pipeline.load("model/randomForestModel")

//    val df=spark.createDataFrame(df1,schema)

//    df.show(false)

//    df1.foreach(f=>{
//      println(f._1)
//      f._2.foreach(a=>{
//        print(a+"\t")
//      })
//      println()
//    })


  }
}
