package in.domain.json

import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.json4s.native.Serialization
import org.json4s.native.Serialization._

import in.domain.User
import in.domain.{UserRepositoryOnMemory, RoomRepositoryOnMemory}

/*
  HandleAsyncEvent Json Parameters
  
  target: Int
  id: Int
  action: Int
  params:
    CreateRoomFormat
    JoinRoomFormat
*/

/*

  Actions

  1 -> CreateRoom
  2 -> JoinRoom
  3 -> ExitRoom
  4 -> ExitServer

*/

object HandleAsyncEventJsonController {
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

  def extractCreateRoomFormat(msg: String): CreateRoomFormat = {
    _extractParams(msg).extract[CreateRoomFormat]
  }

  def extractAuthenticateFormat(msg: String): AuthenticateFormat = {
    _extractParams(msg).extract[AuthenticateFormat]
  }

  def extractJoinRoomFormat(msg: String): JoinRoomFormat = {
    _extractParams(msg).extract[JoinRoomFormat]
  }

  def createSyncResponseMsg(action: Int, result: Boolean, u: User): String = {
    "{\"target\":1,\"src_addr\":\"%s\",\"action\":%d,\"result\":%d}".format(
      u.src_addr, 
      action, 
      if(result) 0 else -1
    )
  }

  private def _parseJson(msg: String) = {
    parse(msg)
  }

  def createLobbyStateMsg(u: User): String = {

    val ur = UserRepositoryOnMemory
    val rr = RoomRepositoryOnMemory
    val users_s = write(ur.getExternalUserList)
    val rooms_s = write(rr.getExternalRoomList)
    "{\"target\":2,\"src_addr\":\"%s\",\"users\":%s,\"rooms\":%s}".format(u.src_addr, users_s, rooms_s)
  }

  def createRoomStateMsg(u: User): String = {

    val ur = UserRepositoryOnMemory
    val rr = RoomRepositoryOnMemory
    val room = rr.getRoomById(u.room)
    "{\"target\":2,\"src_addr\":\"%s\",\"id\":%d,\"roomname\":\"%s\",\"roommaster\":%d,\"members\":\"%s\",\"status\":%d}".format(
      u.src_addr, u.room, room.roomname, room.roommaster, room.members.mkString(","), room.status
    )
  }

  private def _extractParams(msg: String): JValue = {
    (_parseJson(msg) \ "params")
  }

}

// case classes

case class CreateRoomFormat(roomname: String)
case class JoinRoomFormat(id: Int)
case class AuthenticateFormat(username: String, password: String, src_addr: String)
