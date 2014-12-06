package in

import in.infrastructure.iothread.{RecieverThread, SenderThread}
import in.infrastructure._

import in.domain.json.KeepaliveJsonController
import in.domain.{UserRepositoryOnMemory, User}
import in.domain.{RoomRepositoryOnMemory, Room}
import in.Config

object KeepaliveService extends Thread {
  val krmq = KeepaliveRecieveMessageQueueOnMemory
  val smq = SendMessageQueueOnMemory

  val ur = UserRepositoryOnMemory
  val rr = RoomRepositoryOnMemory

  val sleep_keepalive = Config.getInt("sleep.keepalive")

  var running = true

  override def run(): Unit = {
    while(running) {
      val userlist = ur.getUserList
      userlist.map(u => ur.incrementKeepalivecountById(u.identity))
      
      // send
      userlist.map(
        u => smq.push(KeepaliveJsonController.create_keepalive_msg(u))
      )
      
      // recv
      while(! krmq.isEmpty()) {
        val msg = krmq.pop()
        val identity = KeepaliveJsonController.extractIdentity(msg)
        ur.initKeepalivecountById(identity)
      }
      userlist.filter(
        u => (! ur.isAliveById(u.identity))
      ).map(
        u => {
          // room, roomrepositoryから削除する
          if(u.room != -1 && u.room != -2) {
            rr.getRoomById(u.room).removeMember(u.identity)
            if(rr.getRoomById(u.room).members.size == 0) rr.removeRoomById(u.room)
          }

          // userrepository から削除する
          ur.removeUserById(u.identity)
        }
      )
      ur.debugprint()
      Thread.sleep(sleep_keepalive)
    }
  }
}
