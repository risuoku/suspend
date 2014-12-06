package in

import in.infrastructure.iothread.{RecieverThread, SenderThread}
import in.infrastructure._

import in.domain.json.KeepaliveJsonController
import in.domain.{UserRepositoryOnMemory, User}

object KeepaliveService extends Thread {
  //val ksmq = KeepaliveSendMessageQueueOnMemory
  val krmq = KeepaliveRecieveMessageQueueOnMemory
  val smq = SendMessageQueueOnMemory

  val ur = UserRepositoryOnMemory

  var running = true

  override def run(): Unit = {
    while(running) {
      val userlist = ur.getUserList()
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
        u => ur.removeUserById(u.identity)
      )
      ur.debugprint()
      Thread.sleep(5000)
    }
  }
}
