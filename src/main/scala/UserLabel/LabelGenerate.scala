package UserLabel

import org.apache.spark.{SparkConf, SparkContext}

object LabelGenerate{
  val conf=new SparkConf().setMaster("local").setAppName("user-label")
  val sc=new SparkContext(conf)

  /*
  用户
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
    val rdd2=rdd1
      //求出用户和用户词集合
      .map(f=>(f(0),DataAnlysis.anlysis(f(1))))
      //过滤没有评价的用户
      .filter(f=>f._2.length>0)
      //评价分词 (wd,篮球，音乐，游戏）=>(wd，array(篮球，音乐，游戏）)
      .map(f=>{
        (f._1,f._2.split(","))
      })
      //对为数组的数据炸开(wd，array(篮球，音乐，游戏）)=>(wd,篮球)，(wd,音乐）
      .flatMapValues(f=>f)
      //为每个用户和对应的标签赋值  ((wd,篮球)，1)
      .map(f=>((f._1,f._2),1))
      //对相同用户的相同标签做聚合
      .reduceByKey(_+_)
      //聚合之后的结果存入list
      .map(f =>(f._1._1,List[(String,Long)]((f._1._2,f._2))))
      //单个用户的标签list拼接
      .reduceByKey(_:::_)
      //从list取前十位标签入库
      .map(f=>{
        (f._1,f._2.sortBy(_._2).reverse.take(10).map(a=>(a._1+":"+a._2)).mkString)
      })
      rdd2.foreach(println(_))
    //
  }
}
