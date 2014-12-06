package in.domain.json

import org.json4s._
import org.json4s.jackson.JsonMethods._

/*
  Authentication Json Parameters
  
  original
    target: Int
    username: String
    password: String

  src_addr: String
*/

object AuthenticationJsonController {
  implicit val formats = DefaultFormats

  def extractUserOriginal(msg: String): Original = {
    _parseJson((_parseJson(msg) \ "original").extract[String]).extract[Original]
  }

  def extractSrcAddr(msg: String): String = {
    (_parseJson(msg) \ "src_addr").extract[String]
  }

  def createSuccessMsg(src_addr: String, identity: Int): String = {
    "{\"target\":1,\"src_addr\":\"%s\",\"id\":%d}".format(src_addr, identity)
  }

  def createFailureMsg(src_addr: String): String = {
    "{\"target\":1,\"src_addr\":\"%s\",\"id\":-1}".format(src_addr)
  }

  private def _parseJson(msg: String) = {
    parse(msg)
  }
}

case class Original(
  target: Int,
  username: String,
  password: String
)
