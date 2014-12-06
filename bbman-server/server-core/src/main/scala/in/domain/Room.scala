package in.domain

import in.Config
import scala.collection.mutable.ListBuffer

trait Room {
  def identity: Int
  def roomname: String
  def members: List[Int]
  def roommaster: Int
  def status: Int

  def setRoomname(newname: String): String
  def addMember(identity: Int): List[Int]
  def removeMember(identity: Int): Unit
  def setStatus(newstatus: Int): Int

  override def toString: String

  case class ExternalRoom (
    id: Int,
    roomname: String,
    members: String,
    status: Int
  )
  def toExternalRoom: ExternalRoom
}

class RoomOnMemory (
    _identity: Int,
    _roommaster: Int,
    _roomname: String = "default roomname",
    _status: Int = 0
  ) extends Room {

  private var __identity: Int = _identity
  private var __roomname: String = _roomname
  val __members = new ListBuffer[Int](); __members += _roommaster
  private var __roommaster: Int = _roommaster
  private var __status: Int = _status

  def identity: Int = __identity
  def roomname: String = __roomname
  def members: List[Int] = __members.toList
  def roommaster: Int = __roommaster
  def status: Int = __status

  def setRoomname(newname: String): String = {
    __roomname = newname
    __roomname
  }

  def setStatus(newstatus: Int): Int = {
    __status = newstatus
    __status
  }

  def addMember(identity: Int): List[Int] = {
    __members += identity
    __members.toList
  }

  def removeMember(identity: Int): Unit = {
    __members -= identity
    if(roommaster == identity && (! __members.isEmpty)) _setMaster()
  }

  private def _setMaster() = {
    __roommaster = __members.head
  }

  override def toString = {
    "identity: %d".format(identity)
  }

  def toExternalRoom: ExternalRoom = {
    ExternalRoom (
      identity,
      roomname,
      members.mkString(","),
      status
    )
  }
}
