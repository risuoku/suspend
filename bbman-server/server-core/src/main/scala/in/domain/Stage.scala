package in.domain

import in.infrastructure._
import in.domain.stage.{Player, Bomb, Fire}

import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.json4s.JsonDSL._

import scala.collection.mutable.{HashMap, ArraySeq, ListBuffer, ArrayBuffer}
import scala.io.Source
import scala.util.control.Breaks

object Flags {
  var battle_set_continue_flag = true
  var battle_continue_flag = true
}

class Stage {
  val players = scala.collection.mutable.HashMap[Int, Player]()
  var bombs = scala.collection.mutable.ListBuffer[Bomb]()
  val sq = SendMessageQueueOnMemory
  
  val WIDTH:Int = 640
  val HEIGHT:Int = 480
  val PIX_PER_CELL:Int = 32
  val INTERNAL_PER_PIX:Int = 2000
  val INTERNAL_PER_CELL = INTERNAL_PER_PIX * PIX_PER_CELL

  //val BOMB_BLAST_COUNT = 100
  val BOMB_BLAST_COUNT = 130
  val BOMB_BLAST_FORCE_COUNT = 150

  private var _running = true
  def running = _running
  def set_continue = true

  val co_1_0 = internal_coordinate2external_som_index(1*INTERNAL_PER_CELL, 1*INTERNAL_PER_CELL)
  val co_1_1 = internal_coordinate2external_som_index(2*INTERNAL_PER_CELL, 1*INTERNAL_PER_CELL)
  val co_1_2 = internal_coordinate2external_som_index(1*INTERNAL_PER_CELL, 2*INTERNAL_PER_CELL)
  val co_2_0 = internal_coordinate2external_som_index(17*INTERNAL_PER_CELL, 1*INTERNAL_PER_CELL)
  val co_2_1 = internal_coordinate2external_som_index(16*INTERNAL_PER_CELL, 1*INTERNAL_PER_CELL)
  val co_2_2 = internal_coordinate2external_som_index(17*INTERNAL_PER_CELL, 2*INTERNAL_PER_CELL)
  val co_3_0 = internal_coordinate2external_som_index(1*INTERNAL_PER_CELL, 13*INTERNAL_PER_CELL)
  val co_3_1 = internal_coordinate2external_som_index(1*INTERNAL_PER_CELL, 12*INTERNAL_PER_CELL)
  val co_3_2 = internal_coordinate2external_som_index(2*INTERNAL_PER_CELL, 13*INTERNAL_PER_CELL)
  val co_4_0 = internal_coordinate2external_som_index(17*INTERNAL_PER_CELL, 13*INTERNAL_PER_CELL)
  val co_4_1 = internal_coordinate2external_som_index(16*INTERNAL_PER_CELL, 13*INTERNAL_PER_CELL)
  val co_4_2 = internal_coordinate2external_som_index(17*INTERNAL_PER_CELL, 12*INTERNAL_PER_CELL)

  /*
    smap: オブジェクトと識別番号の対応表

    0, 1 -> road
    2 -> hardblock
    3 -> softblock
    4 -> fire(up)
    5 -> fire(down)
    6 -> fire(left)
    7 -> fire(right)
    8 -> fire(center)
    9 -> fire(horizon)
    a -> fire(vertical)
  */
  //val smap = Source.fromFile("/home/risuo/local/projects/hoge/scala-experiment/depricated_bomberman/data/stage_ser_1_1.txt")
  val smap = Source.fromFile("stage_ser_1_1.txt")
  //val smap_original = Source.fromFile("/home/risuo/local/projects/hoge/scala-experiment/depricated_bomberman/data/stage_ser_1.txt")
  val smap_original = Source.fromFile("stage_ser_1.txt")
  val stage_objects_map = scala.collection.mutable.ArrayBuffer[Char]()
  val stage_objects_map_original = scala.collection.mutable.ArrayBuffer[Char]()
  smap.foreach(c => {stage_objects_map += c})
  smap_original.foreach(c => {stage_objects_map_original += c})
  val fires_map = scala.collection.mutable.ArrayBuffer.fill(stage_objects_map.size)(-1)
  val bombs_map = scala.collection.mutable.ArrayBuffer.fill(stage_objects_map.size)(0)
  
  implicit val formats = DefaultFormats

  def add_player(user_id: Int, user_name: String, src_addr: String, user_position: Int):Unit = {
    val xx_1 = 1*INTERNAL_PER_CELL
    val yy_1 = 1*INTERNAL_PER_CELL
    val xx_2 = 17*INTERNAL_PER_CELL
    val yy_2 = 1*INTERNAL_PER_CELL
    val xx_3 = 1*INTERNAL_PER_CELL
    val yy_3 = 13*INTERNAL_PER_CELL
    val xx_4 = 17*INTERNAL_PER_CELL
    val yy_4 = 13*INTERNAL_PER_CELL
    println("user_position: "+user_position)
    players += {
      user_position match {
        case 1 => (user_id -> new Player(user_id, user_name, src_addr, xx_1, yy_1, this))
        case 2 => (user_id -> new Player(user_id, user_name, src_addr, xx_2, yy_2, this))
        case 3 => (user_id -> new Player(user_id, user_name, src_addr, xx_3, yy_3, this))
        case 4 => (user_id -> new Player(user_id, user_name, src_addr, xx_4, yy_4, this))
      }
    }
  }

  def flush_players() = {
    players.keys.toList.map (
      i => players.remove(i)
    )
  }

  // --- APIs for subobjects
  def bombset_or_throwing(x: Int, y: Int, user_id: Int, fire: Int) = {
    bombs += new Bomb(
      round_down_internal(x + INTERNAL_PER_CELL / 2),
      round_down_internal(y + INTERNAL_PER_CELL / 2),
      user_id,
      fire,
      this
    )
    players(user_id).countdown_bomb()
  }

  // --- utilities
  def internal_coordinate2external_som_index(x: Int, y:Int) = {
    val xe = x / INTERNAL_PER_CELL
    val ye = y / INTERNAL_PER_CELL
    ye * (WIDTH / PIX_PER_CELL) + xe
  }

  def round_down_internal(n: Int) = {
    val ne = n / INTERNAL_PER_CELL
    ne * INTERNAL_PER_CELL
  }

  def is_fire(c: Char) = {
    c match {
      case '4'|'5'|'6'|'7'|'8'|'9'|'a' => true
      case _ => false
    }
  }

  // --- control battle
  def init() = {
    stage_objects_map.clear()
    fires_map.clear(); bombs_map.clear()
    val smap_i = Source.fromFile("stage_ser_1_1.txt")
    smap_i.foreach(c => {stage_objects_map += c})

    // player のところは空ける
    stage_objects_map(co_1_0) = stage_objects_map_original(co_1_0)
    stage_objects_map(co_2_0) = stage_objects_map_original(co_2_0)
    stage_objects_map(co_3_0) = stage_objects_map_original(co_3_0)
    stage_objects_map(co_4_0) = stage_objects_map_original(co_4_0)
    stage_objects_map(co_1_1) = stage_objects_map_original(co_1_1)
    stage_objects_map(co_2_1) = stage_objects_map_original(co_2_1)
    stage_objects_map(co_3_1) = stage_objects_map_original(co_3_1)
    stage_objects_map(co_4_1) = stage_objects_map_original(co_4_1)
    stage_objects_map(co_1_2) = stage_objects_map_original(co_1_2)
    stage_objects_map(co_2_2) = stage_objects_map_original(co_2_2)
    stage_objects_map(co_3_2) = stage_objects_map_original(co_3_2)
    stage_objects_map(co_4_2) = stage_objects_map_original(co_4_2)

    stage_objects_map.foreach(c => {fires_map += -1; bombs_map += 0})
    players.map(p => p._2.init_player())
    smap_i.close
    _running = true
  }

  // --- mediator section
  var tmp_count = 0
  def client_keyevent_process(msg_q: Seq[String]) = {
    //players
    for(msg <- msg_q) {
      val json = parse(msg)
      val user_id = (json \ "user_id").extract[Int]
      val keyevent = (json \ "keyevent").extract[Int]
      players(user_id).handle_client_keyevent(keyevent)
    }

    push_msg_to_sq()
  }

  def process() = {
    _blast()

    var len = 0

    // create fires
    for(n <- 0 to stage_objects_map.size - 1) {
      if(is_fire(stage_objects_map(n)) && fires_map(n) == -1) {
        fires_map(n) = 0
        len += 1
      }
      if(fires_map(n) > 5) {
        fires_map(n) = -1
        stage_objects_map(n) = stage_objects_map_original(n)
        len += 1
      } else if(fires_map(n) > -1) {
        fires_map(n) += 1
      }
    }

    // for players
    len += bombs.filterNot(
      b => b.get_state()("count") < BOMB_BLAST_COUNT
    ).map(
      b => b.get_state()("user_id") 
    ).map(u => players(u).countup_bomb()).size

    bombs = bombs.filter(b => b.get_state()("count") < BOMB_BLAST_COUNT)
    bombs.map(b => b.countup_time())

    // bombs_map の更新
    bombs_map.clear
    bombs_map.appendAll(ArrayBuffer.fill(stage_objects_map.size)(0))
    for(b <- bombs) {
      val x = b.get_state()("xx") + INTERNAL_PER_CELL / 2
      val y = b.get_state()("yy") + INTERNAL_PER_CELL / 2
      bombs_map(internal_coordinate2external_som_index(x, y)) = 1
    }
    
    // 何か変化があったらステージ情報をpush
    // 爆破のあった時のみ、生存確認したいので、ここでcheck_alivalする
    if(len > 0) {
      // check alival
      val alival_player_count = players.filter (
        p => p._2.check_alival()
      ).size

      if(alival_player_count < 1) {
        _running = false
      }

      push_msg_to_sq()
    }
  }

  private def _blast() = {
    val br = new Breaks
    br.breakable {
      while(true) {
        val bbb = bombs.filter(
          b => b.get_state()("count") == BOMB_BLAST_COUNT
        )
        if(bbb.size == 0) br.break
        bbb.map(b => b.blast())
        bombs.filter(
          b => is_fire (
            stage_objects_map(
              internal_coordinate2external_som_index(
                b.get_state()("xx") + INTERNAL_PER_CELL / 2,
                b.get_state()("yy") + INTERNAL_PER_CELL / 2
              )
            )
           ) && b.get_state()("count") != BOMB_BLAST_FORCE_COUNT
        ).map(
          c => c.force_countup()
        )
      }
    }
  }

  def push_msg_to_sq() = {
    println("sq: "+sq.size)
    players.map (
      p => sq.push(get_json_stage(p._2.src_addr))
    )
  }
  
  def get_json_stage(src_addr: String): String = {
    val r = scala.collection.immutable.Map[String, String] (
      "target" -> "3",
      "src_addr" -> src_addr,
      "players" -> compact(render(
          players.map(
            p => (p._2.get_external_state())
          )
        )),
      "bombs" -> compact(render(
          bombs.map(
            b => b.get_external_state()
          )
        )),
      "som" -> stage_objects_map.mkString,
      "state" -> (if(running) "0" else "1")
    )
    compact(render(r))
  }

}
