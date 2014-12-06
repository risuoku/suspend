package in.domain.json

import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.json4s.native.Serialization
import org.json4s.native.Serialization._

/*
  Battle Json Parameters
  
  target: Int
  id: Int
  action: Int
  params:
*/

object BattleJsonController {
  implicit val formats = DefaultFormats

  def extractTarget(msg: String): Int = {
    (_parseJson(msg) \ "target").extract[Int]
  }

  def extractIdentity(msg: String): Int = {
    (_parseJson(msg) \ "id").extract[Int]
  }

  def extractAction(msg: String): Int = {
    (_parseJson(msg) \ "action").extract[Int]
  }

  def extractUserFormat(msg: String): UserFormat = {
    _parseJson(msg).extract[UserFormat]
  }

  def createSyncResponseMsg(action: Int, result: Boolean, src_addr: String): String = {
    "{\"target\":1,\"src_addr\":\"%s\",\"action\":%d,\"result\":%d}".format(
      src_addr, 
      action, 
      if(result) 0 else -1
    )
  }
/*
  def createSyncResponseMsg(action: Int, result: Boolean, u: User): String = {
    "{\"target\":1,\"src_addr\":\"%s\",\"action\":%d,\"result\":%d}".format(
      u.src_addr, 
      action, 
      if(result) 0 else -1
    )
  }
*/
  private def _parseJson(msg: String) = {
    parse(msg)
  }

  private def _extractParams(msg: String): JValue = {
    (_parseJson(msg) \ "params")
  }

}

// case classes

case class UserFormat(target: Int, room: Int, id: Int, nickname: String, src_addr: String)
