package UserLabel

import com.alibaba.fastjson.JSON

/**
  * {
  * "reviewPics": null,
  * "extInfoList": [{
  * "title": "contentTags",
  * "values": ["午餐", "分量适中"],
  * "desc": "",
  * "defineType": 0
  * }, {
  * "title": "tagIds",
  * "values": ["684", "240"],
  * "desc": "",
  * "defineType": 0
  * }],
  * "expenseList": null,
  * "reviewIndexes": [2],
  * "scoreList": null
  * }
  */
object DataAnlysis{

  def anlysis(message:String): String ={
    val json=JSON.parseObject(message);
    if(!json.containsKey("extInfoList")||json==null){
      return ""
    }
    val values=json.getJSONArray("extInfoList")
    if(values==null){
      return ""
    }
    val size=values.size()
    val sb=new StringBuilder()
    for(i<- 0 to size-1){
      val json=values.getJSONObject(i)
      if(json!=null&&json.containsKey("title")&&json.getString("title").equals("contentTags")&&json.containsKey("values")){
        val valueArray=json.getJSONArray("values")
        val valueSize=valueArray.size()
        var lag=true
        for(j<- 0 to valueSize-1){
          val value=valueArray.getString(j)
          if(lag){
            lag=false
          }else{
            sb.append(",")
          }
          sb.append(value)
        }
      }
    }
    sb.toString()
  }
}
