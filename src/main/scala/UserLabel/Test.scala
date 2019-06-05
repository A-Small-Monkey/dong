package UserLabel

import java.io.{BufferedReader, File, FileReader}

object Test {
  def main(args: Array[String]): Unit = {
    val fr=new FileReader(new File("data/temptags.txt"))
    val br=new BufferedReader(fr)

    var line="";
    while ((line=br.readLine())!=null){
      val values=line.split("\t")
      if(values.length==2){
        println(DataAnlysis.anlysis(values(1)))
      }
    }
  }
}
