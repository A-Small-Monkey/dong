package MLlib.ClassfierAndRegression

import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.MultilayerPerceptronClassifier
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.feature.{IndexToString, StringIndexer, Word2Vec}
import org.apache.spark.ml.tuning.{CrossValidator, ParamGridBuilder}
import org.apache.spark.sql.SparkSession
import org.apache.spark.{SparkConf, SparkContext}

/**
  *   多层感知分类器
  */
object MultilayerPerceptronClassifierTest {
  def main(args: Array[String]): Unit = {
    val conf=new SparkConf().setMaster("local").setAppName("Multilayer-Perceptron-Classifier")

    val spark=SparkSession.builder().config(conf).getOrCreate()

    val sc=spark.sparkContext

    sc.setLogLevel("error")

    val sourceRdd=sc.textFile("data/SpamMessage/SMSSpamCollection")
      .map(_.split("\t"))
      .filter(_.length==2)
      .map(row=>{
      (row(0),row(1).split(" "))
    })

    //构建源数据
    val df=spark.createDataFrame(sourceRdd).toDF("label","message")

    df.show(false)

    //特征向量的大小
    val vectorSize=20

    //标签索引化
    val stringIndexer=new StringIndexer().setInputCol("label").setOutputCol("indexLabel").fit(df)

    //单词数据向量化
    val word2Vec=new Word2Vec()
      .setInputCol("message")
      .setOutputCol("features")
      .setVectorSize(vectorSize)
      .setMinCount(1)

    //多层感知器的网路配置
    val layers=Array[Int](vectorSize,6,5,2)

    //多层感知器
    val mpc=new MultilayerPerceptronClassifier()
      .setLayers(layers)
      .setBlockSize(256)
      .setSeed(1234l)
      .setLabelCol("indexLabel")
      .setFeaturesCol("features")
      .setPredictionCol("predictionIndexLabel")

    //标签返回原值
    val indexToString=new IndexToString()
      .setInputCol("predictionIndexLabel")
      .setOutputCol("predictionLabel")
      .setLabels(stringIndexer.labels)

    //管道
    val pipeline=new Pipeline()

    //配置管道
    val stages=Array(stringIndexer,word2Vec,mpc,indexToString)
    pipeline.setStages(stages)

    //数据分离
    val (Array(train,test))=df.randomSplit(Array(0.8,0.2))

    //训练模型
    //val pipelineModel=pipeline.fit(train)

    //模型测试
    //val testResultDF=pipelineModel.transform(test)

    //结果评估求值器

    val multiclassEvaluator=new MulticlassClassificationEvaluator()
      .setLabelCol("indexLabel")
      .setPredictionCol("predictionIndexLabel")
      .setMetricName("accuracy")

    //预测结果的精度
//    val accuracy=multiclassEvaluator.evaluate(testResultDF)

//    println(accuracy)

    val paramMaps=new ParamGridBuilder()
      .addGrid(word2Vec.maxIter,Array(64,128))
      .addGrid(mpc.maxIter,Array(64,128))
      .build()

    //交叉验证会测试的模型数量为：参数map的 数量积
    //Fold是交查验证数据的树蕨切分方式，数据切分为K分，k-1份用来训练，剩下的一份用来测试,并进行k次不同的组合
    val crossValidator=new CrossValidator()
        .setEstimator(pipeline)
        .setEvaluator(multiclassEvaluator)
        .setEstimatorParamMaps(paramMaps)
        .setNumFolds(3)


    //交叉验证训练模型
    val crossValidatorModel=crossValidator.fit(train)

    //测试
    crossValidatorModel.transform(test).show(false)

    println("每个模型的平均监控指标")
    crossValidatorModel.avgMetrics.foreach(println(_))
    println("每个模型的参数")
    crossValidatorModel.bestModel.params.foreach(f=>{
      println(f.name)
    })

//    testResultDF.show(50,false)
  }
}




