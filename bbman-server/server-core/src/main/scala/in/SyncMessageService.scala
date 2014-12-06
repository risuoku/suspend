package in

import in.infrastructure._
import in.infrastructure.iothread.SyncMessageServer
import in.domain.UserFactory
import in.domain.UserRepositoryOnMemory
import in.domain.RoomRepositoryOnMemory
import in.domain.BattleRepositoryOnMemory
import in.domain.json.HandleAsyncEventJsonController
import in.domain.json.AuthenticationJsonController

object SyncMessageService extends Thread {
  val json_c = HandleAsyncEventJsonController
  val rr = RoomRepositoryOnMemory
  val ur = UserRepositoryOnMemory
  val br = BattleRepositoryOnMemory

  var running = true

  override def run(): Unit = {
    SyncMessageServer.start(handle)
  }

  def handle(msg: String): String = {
    val action = json_c.extractAction(msg)
    val identity_u = json_c.extractIdentity(msg)

    action match {
      case 0 => _authenticateAction(msg)
      case 1 => _createRoomAction(identity_u, msg)
      case 2 => _joinRoomAction(identity_u, msg)
      case 3 => _exitRoomAction(identity_u)
      case 4 => _exitServerAction(identity_u)
      case 5 => _startBattleAction(identity_u)
      case 6 => _endBattleAction(identity_u)
      case 7 => _getBattleStateAction(identity_u)
    }
  }

  private def _authenticateAction(msg: String): String = {
    val fmt = json_c.extractAuthenticateFormat(msg)
    val username = fmt.username
    val password = fmt.password
    val src_addr = fmt.src_addr
    
    try {
      val user = ur.authenticate(username, password, src_addr)
      ur.setUser(user)
      AuthenticationJsonController.createSuccessMsg (
        src_addr, 
        user.identity
      )
    } catch {
      case _ => AuthenticationJsonController.createFailureMsg (
        src_addr
      )
    }
  }

  private def _createRoomAction(identity: Int, msg: String): String = {
    val roomname = json_c.extractCreateRoomFormat(msg).roomname

    try {
      val room = rr.createRoom(roomname, identity)
      val now_user = ur.getUserById(identity)
      ur.setUser(
        UserFactory.create(
          identity,
          now_user.src_addr,
          now_user.username,
          now_user.nickname,
          now_user.point,
          room.identity
        )
      )
      json_c.createSyncResponseMsg(1, true, ur.getUserById(identity))
    } catch {
      case _ => {
        json_c.createSyncResponseMsg(1, false, ur.getUserById(identity))
      }
    }
  }

  private def _joinRoomAction(identity: Int, msg: String): String = {
    val identity_r = json_c.extractJoinRoomFormat(msg).id

    try {
      val now_user = ur.getUserById(identity)
      rr.getRoomById(identity_r).addMember(identity)
      ur.setUser(
        UserFactory.create(
          identity,
          now_user.src_addr,
          now_user.username,
          now_user.nickname,
          now_user.point,
          identity_r
        )
      )
      json_c.createSyncResponseMsg(2, true, ur.getUserById(identity))
    } catch {
      case ex => {
        println(ex)
        json_c.createSyncResponseMsg(2, false, ur.getUserById(identity))
      }
    }
  }

  private def _exitRoomAction(identity: Int): String = {
    try {
      val now_user = ur.getUserById(identity)
      val identity_r = now_user.room
      rr.getRoomById(identity_r).removeMember(identity)
      ur.setUser(
        UserFactory.create(
          identity,
          now_user.src_addr,
          now_user.username,
          now_user.nickname,
          now_user.point,
          -1
        )
      )
      if (rr.getRoomById(identity_r).members.size == 0) rr.removeRoomById(identity_r)
      json_c.createSyncResponseMsg(3, true, ur.getUserById(identity))
    } catch {
      case ex => {
        println(ex)
        json_c.createSyncResponseMsg(3, false, ur.getUserById(identity))
      }
    }
  }

  private def _exitServerAction(identity: Int): String = {
    try {
      json_c.createSyncResponseMsg(4, true, ur.getUserById(identity))
    } catch {
      case _ => {
        json_c.createSyncResponseMsg(4, false, ur.getUserById(identity))
      }
    }
  }

  private def _startBattleAction(identity: Int): String = {
    try {
      val identity_r = ur.getUserById(identity).room
      rr.getRoomById(identity_r).setStatus(2)
      br.startById(identity_r)
      json_c.createSyncResponseMsg(5, true, ur.getUserById(identity))
    } catch {
      case _ => {
        json_c.createSyncResponseMsg(5, false, ur.getUserById(identity))
      }
    }
  }

  private def _endBattleAction(identity: Int): String = {
    try {
      val identity_r = ur.getUserById(identity).room
      rr.getRoomById(identity_r).setStatus(0)
      br.shutdownById(identity_r)
      json_c.createSyncResponseMsg(6, true, ur.getUserById(identity))
    } catch {
      case _ => {
        json_c.createSyncResponseMsg(6, false, ur.getUserById(identity))
      }
    }
  }

  private def _getBattleStateAction(identity: Int): String = {
    try {
      val identity_r = ur.getUserById(identity).room
      val src_addr = ur.getUserById(identity).src_addr
      val state = br.getBattleById(identity_r).stage_state(src_addr)
      state
    } catch {
      case _ => {
        json_c.createSyncResponseMsg(7, false, ur.getUserById(identity))
      }
    }
  }
}
