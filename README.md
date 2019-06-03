# dong
My Test1

一.基于交变的最小二乘数ALS实现电影系统的推荐
    1.首先需要一个用户看电影的行为集合数据
    （user,movie,time,rate)
    2.将数据集切分为训练集合和测试集合randomSpilt()
    3.NLS配置输入用户，产品和评分。
    4.通过fit训练一个ALSModel
    5.ALSModel根据用户和产品推荐相关程度前十位的电影和用户
二.基于购物评价平台的用户标签的建立
    1.分析商户的评价信息进行商户的标签的划分
    （商户；JSON(评价)
    2.取出评价信息
    3.按商户分类，求出每个商户的前十位标签。
三。PipeLine
    管道内部可以理解为一系列的 transform和计算
    Tokenizer对文本分词
    StopWordRemover去除文本停用词
    计算word的HashingTF
    LogisticRegression模型估算器
    传入转换器和计算器
    pipeline.setStages(Array(tokenizer,stopWordRemove,hashingTF, lr))
四。Word2Vec 单词向量。
  * 1.由seq序列数据生成分词之后的数组流 documentDF
  * 2.设置word2Vec向量 输入col 输出col 输出向量大小，输出总数
  * 3.word2Vec fit文档，生成该documentDF的Word2VecModel
  * 4.documentDF文档对应的Word2VecModel transform 转换为最终输出结果的DF