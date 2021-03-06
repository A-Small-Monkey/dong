SparkdMLlib的随机森林算法：

随机森林由多个决策树生成
决策树可以理解为一个分类器，对数据进行分类
决策树生成过程：
    1.记录作为一个节点
    2.节点寻找合适的切割点
    3.利用切割点分为两个子节点
    4.对子节点重复执行 2.3直到结束
    
主要涉及的API和源码思想
tree/RandomForest:
val
    1.Strategy:计算策略，存储树的参数配置
    2.numTree：决策树的数量
    3.seed：种子
method
trainClassifier:=>训练RandomForestModel
    RDD[LabelPoint]：源数据
    numClass：
    Impurities:纯度计算方式
    numTrees:树的数量
    categoricalFeaturesInfo Map[Int,Int]:绝对的特征信息，categoricalFeaturesInfo 为空，意味着所有的特征为连续型变量
    featureSubsetStrategy：特征子集采样策略
    maxDepth:树的深度
    maxBins：装箱数量
    最后生成一个RandomForest rf.run()=>impl/RandomForest.run()内部为具体的随机森林的算法
 
    
    最重要的两步：
        1.最佳切分装箱findSplits=>装箱binning
        2.最优切分点的选择findBeastSplits=>spilts
        
        对于变量如何选择最佳的切分点：
        1.对于离散型变量
            对于有序的离散型特征值，假如有5个特征值，则最多只能划分5-1
            对于无序的离散型特征值，加入有5个特征值，则最多可以划分2的4次方-1  
        2.对于连续型变量可用<= ，<，>,>=
        3.对于Boolean型变量，可用=号划分
    
    
