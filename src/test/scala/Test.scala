import java.text.SimpleDateFormat

object Test {
  def main(args: Array[String]): Unit = {
    println(System.currentTimeMillis().toString)
    val time = System.currentTimeMillis().toString.substring(0, 10).toLong
    println(time)
    val currentTime: String = new SimpleDateFormat("yyyyMMddHHmmss").format(time * 1000)
    println(currentTime)
  }
}
