package in.domain

import org.specs2.mutable._
import org.specs2.mock.Mockito

import scala.collection.mutable.HashMap

import in.domain.{User, UserFactory}
import in.Config

object RoomOnMemorySpec extends Specification with Mockito {

  sequential

  "RoomOnMemory#setRoomname" should {

    "setRoomname(\"hogefuga\")したらhogefugaが設定される" in {
      val rom = new RoomOnMemory(1, 123)
      rom.setRoomname("hogefuga")
      rom.roomname must be_==("hogefuga")
    }
  }

  "RoomOnMemory#addMember" should {

    "memberが追加される" in {
      val rom = new RoomOnMemory(1, 123)
      rom.addMember(777) must contain (123, 777)
    }
  }

  "RoomOnMemory#removeMember" should {
    "memberが削除される" in todo
  }

  // contexts
  trait context1 extends Before {
    val rom = new RoomOnMemory(1, 123)

    def before = {
      rom.addMember(777)
      rom.addMember(54)
    }
  }
}
