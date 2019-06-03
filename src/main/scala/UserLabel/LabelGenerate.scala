package UserLabel

import org.apache.spark.{SparkConf, SparkContext}

object LabelGenerate{
  val conf=new SparkConf().setMaster("local").setAppName("user-label")
  val sc=new SparkContext(conf)

  /*
  77287793	{"reviewPics":null,"extInfoList":null,"expenseList":null,"reviewIndexes":[1,2],"scoreList":null}
  77287793	{"reviewPics":null,"extInfoList":null,"expenseList":null,"reviewIndexes":[1,2],"scoreList":null}
  77287793	{"reviewPics":null,"extInfoList":null,"expenseList":null,"reviewIndexes":[1,2],"scoreList":null}
  77287793	{"reviewPics":null,"extInfoList":null,"expenseList":null,"reviewIndexes":[1,2],"scoreList":null}
  77287793	{"reviewPics":null,"extInfoList":null,"expenseList":null,"reviewIndexes":[1,2],"scoreList":null}
   */
  def main(args: Array[String]): Unit = {
    val srcData=sc.textFile("data/temptags.txt")
    //过滤长度不为2的数据
    val rdd1=srcData.map(a=>(a.split("\t"))).filter(f=>(f.length==2))
    val rdd2=rdd1.map(f=>(f(0),DataAnlysis.anlysis(f(1))))
      .filter(f=>f._2.length>0)
      .map(f=>{
        (f._1,f._2.split(","))
      })
      .flatMapValues(f=>f)
      .map(f=>((f._1,f._2),1))
      .reduceByKey(_+_)
      .map(f =>(f._1._1,List[(String,Long)]((f._1._2,f._2))))
      .reduceByKey(_:::_)
      .map(f=>{
        (f._1,f._2.sortBy(_._2).reverse.take(10).map(a=>(a._1+":"+a._2)).mkString)
      })
    rdd2.foreach(println(_))
    //
  }
}
