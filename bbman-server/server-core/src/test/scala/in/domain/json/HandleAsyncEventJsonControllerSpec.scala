package in.domain.json

import org.specs2.mutable._
import org.specs2.mock.Mockito

import in.domain.json.{HandleAsyncEventJsonController}
import in.domain.{UserRepositoryOnMemory, RoomRepositoryOnMemory}
import in.domain.{UserFactory, RoomFactory}

/*
  HandleAsyncEvent Json Parameters

  target: Int
  id: Int
  action: Int
  params:
    CreateRoomFormat
    JoinRoomFormat
*/


object HandleAsyncEventJsonControllerSpec extends Specification with Mockito {
  
  sequential

  "HandleAsyncEventJsonControllerSpec#extractIdentity" should {

    "identity が正しく取得されている" in new CreateRoomContext {
      json_c.extractIdentity(msg1) must be_==(123)
    }
  }

  "HandleAsyncEventJsonControllerSpec#extractAction" should {

    "action が正しく取得されている" in new CreateRoomContext {
      json_c.extractAction(msg1) must be_==(1)
    }
  }

  "HandleAsyncEventJsonControllerSpec#extractCreateRoomFormat" should {

    "CreateRoomForamat が正しく取得されている" in new CreateRoomContext {
      json_c.extractCreateRoomFormat(msg1).roomname must be_==("testroom1")
    }
  }

  "HandleAsyncEventJsonControllerSpec#extractJoinRoomFormat" should {
    "JoinRoomFormat が正しく取得されている" in new Context1 {
      val msg1 = "{\"target\":1, \"id\":123, \"action\":2, \"params\":{\"id\":54}}"
      json_c.extractJoinRoomFormat(msg1).id must be_==(54)
    }
  }

  "HandleAsyncEventJsonControllerSpec#createSyncResponseMsg" should {
    "SyncResopnseMsg が正しく生成されている" in new SyncResponseContext {
      val ans1 = "{\"target\":1,\"src_addr\":\"127.0.0.1\",\"action\":3,\"result\":0}"
      val ans2 = "{\"target\":1,\"src_addr\":\"192.168.0.1\",\"action\":2,\"result\":-1}"
      val ans3 = "{\"target\":1,\"src_addr\":\"192.168.11.11\",\"action\":1,\"result\":0}"

      json_c.createSyncResponseMsg(3, true, m1) must be_==(ans1)
      json_c.createSyncResponseMsg(2, false, m2) must be_==(ans2)
      json_c.createSyncResponseMsg(1, true, m3) must be_==(ans3)
    }
  }

  "HandleAsyncEventJsonControllerSpec#createLobbyStateMsg" should {

    "LobbyStateMsg が正しく生成されている" in new LobbyStateContext {
      val u_list_s = "[{\"id\":2,\"nickname\":\"ogamansan\",\"point\":10000,\"room\":-1},{\"id\":1,\"nickname\":\"risuosan\",\"point\":7000,\"room\":5}]"
      val r_list_s = "[{\"id\":7,\"roomname\":\"testroom\",\"members\":\"99\",\"status\":0},{\"id\":9,\"roomname\":\"hogefugafuga\",\"members\":\"99\",\"status\":0}]"
      val ans = "{\"target\":2,\"src_addr\":\"127.0.0.1\",\"users\":%s,\"rooms\":%s}".format(u_list_s, r_list_s)

      json_c.createLobbyStateMsg(m1) must be_==(ans)
    }
  }

  "HandleAsyncEventJsonControllerSpec#createRoomStateMsg" should {

    "RoomStateMsg が正しく生成されている" in new LobbyStateContext {
      val ans1 = "{\"target\":2,\"src_addr\":\"127.0.0.1\",\"id\":7,\"roomname\":\"testroom\",\"roommaster\":99,\"members\":\"99\",\"status\":0}"

      json_c.createRoomStateMsg(m1) must be_==(ans1)
    }
  }

  // contexts
  trait Context1 extends BeforeAfter {
    val json_c = HandleAsyncEventJsonController

    def before = {}
    def after = {}
  }

  trait CreateRoomContext extends BeforeAfter {
    val json_c = HandleAsyncEventJsonController
    val msg1 = "{\"target\":1, \"id\":123, \"action\":1, \"params\":{\"roomname\":\"testroom1\"}}"

    def before = {}
    def after = {}
  }

  trait LobbyStateContext extends BeforeAfter {
    val json_c = HandleAsyncEventJsonController
    
    val ur = UserRepositoryOnMemory
    val rr = RoomRepositoryOnMemory

    val m1 = mock[in.domain.User]

    def before = {
      val u1 = UserFactory.create(1, "localhost", "aaa", "risuosan", 7000, 5)
      val u2 = UserFactory.create(2, "localhost", "aaa", "ogamansan", 10000, -1)
      val r1 = RoomFactory.create(7, "testroom", 99)
      val r2 = RoomFactory.create(9, "hogefugafuga", 99)
      ur.setUser(u1); ur.setUser(u2)
      rr.setRoom(r1); rr.setRoom(r2)
      m1.src_addr returns "127.0.0.1"
      m1.room returns 7
    }

    def after = {
      ur.removeUserById(1); ur.removeUserById(2)
      rr.removeRoomById(7); rr.removeRoomById(9)
    }
  }

  trait SyncResponseContext extends Before {
    val json_c = HandleAsyncEventJsonController
    val m1 = mock[in.domain.User]
    val m2 = mock[in.domain.User]
    val m3 = mock[in.domain.User]

    def before = {
      m1.src_addr returns "127.0.0.1"
      m2.src_addr returns "192.168.0.1"
      m3.src_addr returns "192.168.11.11"
    }
  }
}
