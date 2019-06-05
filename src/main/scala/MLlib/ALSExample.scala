// scalastyle:off println
package MLlib

// $example on$
import org.apache.log4j.lf5.LogLevel
import org.apache.spark.ml.evaluation.RegressionEvaluator
import org.apache.spark.ml.recommendation.ALS
import org.apache.spark.sql.SparkSession

/**
  * 基于交变最小二乘数的推荐
  */
object ALSExample {
  //电影数据：用户名，电影i，评分，时间
  case class Rating(userId: Int, movieId: Int, rating: Float, timestamp: Long)
  //数据切分和整理
  def parseRating(str: String): Rating = {
    val fields = str.split("::")
    assert(fields.size == 4)
    Rating(fields(0).toInt, fields(1).toInt, fields(2).toFloat, fields(3).toLong)
  }
  def main(args: Array[String]) {
    val spark = SparkSession
      .builder
      .master("local")
      .appName("ALSExample")
      .getOrCreate()
    import spark.implicits._

    val sc=spark.sparkContext
    sc.setLogLevel(LogLevel.ERROR.toString)

    //读取数据源并整理数据
    val ratings = spark.read.textFile("C:\\Users\\dong\\Downloads\\sample_movielens_ratings.txt").map(parseRating).toDF()
    //将数据集切分为 训练集和测试集
    val Array(training, test) = ratings.randomSplit(Array(0.8, 0.2))

    //使用ALS在训练集数据上构建推荐模型 =>测量评级预测的均方根误差来评估推荐模型
    val als = new ALS().setMaxIter(5).setImplicitPrefs(false).setRegParam(0.01).setUserCol("userId").setItemCol("movieId").setRatingCol("rating")
    val model = als.fit(training)

    // 通过计算rmse(均方根误差)来评估模型
    // 为确保不获取到NaN评估参数，我们将冷启动策略设置为drop。
    val predictions = model.transform(test)

    val evaluator = new RegressionEvaluator().setMetricName("rmse").setLabelCol("rating").setPredictionCol("prediction")
    val rmse = evaluator.evaluate(predictions)
    println(s"Root-mean-square error = $rmse")

    //每个用户推荐的前十个电影
    val userRecs = model.recommendForAllUsers(10)
    //每个电影推荐的十个用户
    val movieRecs = model.recommendForAllItems(10)
    userRecs.foreach(f=> {
      print(f(0)+"\t")
      println(f(1))
    })
    movieRecs.foreach(f=>{
      (print(f(0)))
      println(f(1))
    })
    spark.stop()
  }
}
// scalastyle:on println

