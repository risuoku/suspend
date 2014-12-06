package in

import in.infrastructure.iothread.{RecieverThread, SenderThread}
import in.{KeepaliveService, SyncMessageService}

object GatewayService {
  def main(args: Array[String]): Unit = {
    RecieverThread.start()
    SenderThread.start()
    SyncMessageService.start()
    KeepaliveService.start()
  }
}
