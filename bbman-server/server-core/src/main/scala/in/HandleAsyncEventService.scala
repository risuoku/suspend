package in

import in.infrastructure._
import in.domain.UserFactory
import in.domain.UserRepositoryOnMemory
import in.domain.RoomRepositoryOnMemory
import in.domain.json.HandleAsyncEventJsonController
import in.Config

object HandleAsyncEventService extends Thread {
  val smq = SendMessageQueueOnMemory
  val json_c = HandleAsyncEventJsonController
  val rr = RoomRepositoryOnMemory
  val ur = UserRepositoryOnMemory

  val sleep_async_event = Config.getInt("sleep.async_event")

  var running = true

  override def run(): Unit = {
    while(running) {
      val userlist_lobby = ur.getUserList.filter (
        u => u.room == -1
      )
      val userlist_room = ur.getUserList.filter (
        u => u.room > 0
      )
      
      // send
      userlist_lobby.map (
        u => smq.push (
          json_c.createLobbyStateMsg(u)
        )
      )
      userlist_room.map (
        u => smq.push (
          json_c.createRoomStateMsg(u)
        )
      )

      // recv
      // Nothing
      Thread.sleep(sleep_async_event)
    }
  }
}
