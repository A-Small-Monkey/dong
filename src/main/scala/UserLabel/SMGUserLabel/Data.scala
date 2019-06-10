package UserLabel.SMGUserLabel

import java.util

import org.ansj.splitWord.analysis.{NlpAnalysis, ToAnalysis}

object Data {

  def getWordData(): util.ArrayList[String] ={
    val data=SMGData.getData
    val msgData=data.iterator()
    var wordList=new util.ArrayList[String]

    while (msgData.hasNext){
      val s=msgData.next()
      val term=ToAnalysis.parse(s)
      val termIterator=term.iterator()
      var sb=new StringBuilder()
      var flag=true
      while (termIterator.hasNext){
        val word=termIterator.next().getName
        if(flag){
          flag=false
        }else{
          sb.append(",")
        }
        sb.append(word)
      }
      val words=sb.toString()
      println(words)
      wordList.add(words)
    }
    wordList
  }
}
