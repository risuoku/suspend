package in.infrastructure.json

import org.json4s._
import org.json4s.jackson.JsonMethods._

/*
  Json Parameters

  src_addr: String
*/

object SendJsonController {
  implicit val formats = DefaultFormats

  def extractSrcAddr(msg: String): String = {
    //println((_parseJson(msg) \ "src_addr").extract[String])
    (_parseJson(msg) \ "src_addr").extract[String]
  }

  private def _parseJson(msg: String) = {
    parse(msg)
  }
}
