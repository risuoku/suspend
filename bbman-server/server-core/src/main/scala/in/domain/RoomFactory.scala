package in.domain

import in.domain.{Room}
import in.domain.{RoomRepository}

trait RoomFactory {
  def create(
    identity: Int, 
    roomname: String,
    roommaster: Int
  ): Room
}

object RoomFactory {
  def create (
    identity: Int, 
    roomname: String,
    roommaster: Int
  ): Room = {
    val INIT_STATUS = 0
    new RoomOnMemory (
      identity,
      roommaster,
      roomname,
      INIT_STATUS
    )
  }
}
