package in.domain

import in.infrastructure.iothread.{RecieverThread, SenderThread}
import in.infrastructure._

import in.domain.json.BattleJsonController
//import in.domain.json.HandleSyncEventJsonController
import in.domain.Stage
import in.domain.{UserRepositoryOnMemory, RoomRepositoryOnMemory}

import in.Config

class Battle(_room: Int) extends Thread {
  var battle_set_running = true
  val stage = new Stage
  val room = _room

  val brq = BattleRecieveMessageQueueOnMemory.queues(room)
  //val hsrq = HandleSyncEventRecieveMessageQueueOnMemory.queues(room)
  val sq = SendMessageQueueOnMemory
  val ur = UserRepositoryOnMemory
  val rr = RoomRepositoryOnMemory

  val json_c_battle = BattleJsonController
  //val json_c_sync = HandleSyncEventJsonController
  
  override def run() = {
    val sleep_battle_frame = Config.getInt("sleep.battle.frame")

    initPlayers()

    while(battle_set_running) {
      println("stage init..")
      stage.init()

      while(! brq.isEmpty) {brq.pop()}
      while(stage.running) {
        //if(! brq.isEmpty) {
          val tmplist = new scala.collection.mutable.ListBuffer[String]()
          while(! brq.isEmpty) tmplist += brq.pop()
          stage.client_keyevent_process(tmplist.toSeq)
        //}
        stage.process()
        Thread.sleep(sleep_battle_frame)
      }

      Thread.sleep(3000)

      if(true) {
      //if(! stage.set_continue) {
        battle_set_running = false
      }
    }
  }

  def initPlayers() = {
    val members: List[Int] = rr.getRoomById(room).members
    
    var n: Int = 1
    for(m <- members) {
      val u = ur.getUserById(m)
      stage.add_player (
        u.identity,
        u.nickname,
        u.src_addr,
        n
      )
      n += 1
    }
  }

  def shutdown() = {
    battle_set_running = false
  }

  def stage_state(src_addr: String): String = {
    stage.get_json_stage(src_addr)
  }

  private def _notifyBattleStart(src_addr: String) = {
    sq.push(json_c_battle.createSyncResponseMsg(-1, true, src_addr))
  }

  private def _notifyBattleEnd(src_addr: String) = {
    sq.push(json_c_battle.createSyncResponseMsg(-1, true, src_addr))
  }
}
