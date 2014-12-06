package in.domain

import org.specs2.mutable._
import org.specs2.mock.Mockito

import scala.collection.mutable.HashMap

import in.domain.{RoomRepository}
import in.domain.Room
import in.Config

object RoomRepositorySpec extends Specification with Mockito {

  sequential

  "RoomRepositoryOnMemory#createRoom" should {
    
    "Roomが作成される" in new context1 {
      val r = rr.createRoom (
        "hoge", 123
      )
      r.roomname must be_==("hoge")
      r.roommaster must be_==(123)
      r.identity must be_==(4)
      rr.removeRoomById(2)
      rr.createRoom("fuga", 234).identity must be_==(2)
    }
  }

  "RoomRepositoryOnMemory#removeRoomById" should {

    "Roomがidentityによってremoveされる" in new context1 {
      rr.removeRoomById(143) must be_==(143)
      rr.rooms(143) must throwA[NoSuchElementException]
    }
  }

  "RoomRepositoryOnMemory#getRoomById" should {
    "Roomをidentityによって取得する" in new context1 {
      rr.getRoomById(3).roomname must be_==("testroom3")
      rr.getRoomById(3).roommaster must be_==(3)
    }
  }

  "RoomRepositoryOnMemory#setRoomnameById" should {
    "identityで指定したRoomのroomnameを更新する" in todo
  }

  "RoomRepositoryOnMemory#addMemberById" should {
    "identityで指定したRoomのroommemberを更新する" in todo
  }

  "RoomRepositoryOnMemory#removeMemberById" should {
    "identityで指定したRoomのroommemberを削除する" in todo
  }

  // contexts
  trait context1 extends BeforeAfter {
    val rr = RoomRepositoryOnMemory

    def before = {
      rr.createRoom("testroom1", 34)
      rr.createRoom("testroom2", 143)
      rr.createRoom("testroom3", 3)
    }

    def after = {
      rr.getRoomList.map(
        r => r.identity
      ).map(
        i => rr.removeRoomById(i)
      )
    }
  }
}
