package in

import in.infrastructure.iothread.{RecieverThread, SenderThread, SyncMessageServer}
import in.infrastructure._

import in.domain.json.AuthenticationJsonController
import in.domain.UserRepositoryOnMemory

object SyncMessageService extends Thread {
  val ur = UserRepositoryOnMemory

  override def run() = {
    SyncMessageServer.start(handle)
  }

  def handle(msg: String): String = {
    val username = AuthenticationJsonController.extractUserOriginal(msg).username
    val password = AuthenticationJsonController.extractUserOriginal(msg).password
    val src_addr = AuthenticationJsonController.extractSrcAddr(msg)
    
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
}
