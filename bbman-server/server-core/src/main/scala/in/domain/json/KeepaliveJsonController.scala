package in.domain.json

import org.json4s._
import org.json4s.jackson.JsonMethods._
import in.domain.User

/*
  Keepalive Json Parameters
  
  target: Int
  identity: Int
*/

object KeepaliveJsonController {
  implicit val formats = DefaultFormats

  def extractTarget(msg: String): Int = {
    (_parseJson(msg) \ "target").extract[Int]
  }

  def extractIdentity(msg: String): Int = {
    (_parseJson(msg) \ "id").extract[Int]
  }

  private def _parseJson(msg: String) = {
    parse(msg)
  }

  def create_keepalive_msg(u: User): String = {
    "{\"target\":0, \"src_addr\":\"%s\",\"room\":%d}".format(u.src_addr, u.room)
  }
}
