package in.infrastructure.json

import org.json4s._
import org.json4s.jackson.JsonMethods._

/*
  Json Parameters

  target: Int
  event: Int
*/

object RecieveJsonController {
  implicit val formats = DefaultFormats

  def extractTarget(msg: String): Int = {
    (_parseJson(msg) \ "target").extract[Int]
  }

  def extractRoom(msg: String): Int = {
    (_parseJson(msg) \ "room").extract[Int]
  }

  private def _parseJson(msg: String) = {
    parse(msg)
  }
}
