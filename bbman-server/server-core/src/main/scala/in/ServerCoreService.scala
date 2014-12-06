package in

import in.infrastructure.iothread.{RecieverThread, SenderThread}
import in.{KeepaliveService, HandleAsyncEventService, SyncMessageService}

object ServerCoreService {
  def main(args: Array[String]): Unit = {
    RecieverThread.start()
    SenderThread.start()
    SyncMessageService.start()
    KeepaliveService.start()
    HandleAsyncEventService.start()
  }
}
