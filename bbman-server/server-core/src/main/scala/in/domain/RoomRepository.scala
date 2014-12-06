package in.domain

import in.Config
import scala.collection.mutable.HashMap

import in.domain.{Room, RoomOnMemory}
import in.domain.{RoomFactory}

trait RoomRepository {
  def createRoom(roomname: String, roommaster: Int): Room
  def setRoom(room: Room): Room
  def removeRoomById(identity: Int): Int
  def getRoomById(identity: Int): Room
  def getRoomList: List[Room]
}

object RoomRepositoryOnMemory extends RoomRepository {
  val rooms = new HashMap[Int, Room]
  val MAX_ROOM_COUNT = 50

  def createRoom(roomname: String, roommaster: Int): Room = {
    val sri = rooms.toList.map (
      m => m._1
    )
    val identity = (1 to MAX_ROOM_COUNT).toList.filter (
      i => (! sri.contains(i))
    ).min
    setRoom (
      RoomFactory.create(identity, roomname, roommaster)
    )
  }

  def setRoom(room: Room): Room = {
    rooms(room.identity) = room
    room
  }

  def removeRoomById(identity: Int): Int = {
    rooms.remove(identity)
    identity
  }

  def getRoomById(identity: Int): Room = {
    rooms(identity)
  }

  def getRoomList: List[Room] = {
    rooms.toList.map (
      r => r._2
    )
  }

  def getExternalRoomList = {
    getRoomList.map (
      r => r.toExternalRoom
    )
  }
}
