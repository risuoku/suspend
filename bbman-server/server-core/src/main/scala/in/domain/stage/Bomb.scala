package in.domain.stage

import scala.util.control.Breaks
import scala.collection.mutable.HashMap
import in.domain.Stage


class Bomb(x: Int, y: Int, user_id: Int, fire: Int, _stage: Stage) {
  val m = HashMap[String, Int] (
    "xx" -> x,
    "yy" -> y,
    "user_id" -> user_id,
    "fire" -> fire,
    "count" -> 0,
    "move_state" -> 0,
    "direction" -> 0
  )

  val stage = _stage

  def get_state() = {
    m
  }

  def get_external_state() = {
    Map[String, Int] (
      "xx" -> m("xx")/stage.INTERNAL_PER_PIX,
      "yy" -> m("yy")/stage.INTERNAL_PER_PIX
    )
  }

  def countup_time() = {
    m("count") += 1
  }

  def force_countup() = {
    m("count") = stage.BOMB_BLAST_COUNT
    //print(m("xx")+" "); println(m("yy"))
  }

  def blast() = {
    val nindex = stage.internal_coordinate2external_som_index(
      m("xx") + stage.INTERNAL_PER_CELL / 2,
      m("yy") + stage.INTERNAL_PER_CELL / 2
    )

    m("count") = stage.BOMB_BLAST_FORCE_COUNT
    
    stage.stage_objects_map(nindex) = '4'
    
    val b = new Breaks

    // UP
    b.breakable {
      for(i <- 1 to m("fire")) {
        if(stage.stage_objects_map(nindex - i * stage.WIDTH / stage.PIX_PER_CELL) == '2') b.break
        else if (stage.stage_objects_map(nindex - i * stage.WIDTH / stage.PIX_PER_CELL) == '3'){
          stage.stage_objects_map(nindex - i * stage.WIDTH / stage.PIX_PER_CELL) = stage.stage_objects_map_original(nindex - i * stage.WIDTH / stage.PIX_PER_CELL)
          b.break
        }
        else {
          stage.stage_objects_map(nindex - i * stage.WIDTH / stage.PIX_PER_CELL) = '4'
        }
      }
    }

    // DOWN
    b.breakable {
      for(i <- 1 to m("fire")) {
        if(stage.stage_objects_map(nindex + i * stage.WIDTH / stage.PIX_PER_CELL) == '2') b.break
        else if (stage.stage_objects_map(nindex + i * stage.WIDTH / stage.PIX_PER_CELL) == '3'){
          stage.stage_objects_map(nindex + i * stage.WIDTH / stage.PIX_PER_CELL) = stage.stage_objects_map_original(nindex + i * stage.WIDTH / stage.PIX_PER_CELL)
          b.break
        }
        else {
          stage.stage_objects_map(nindex + i * stage.WIDTH / stage.PIX_PER_CELL) = '4'
        }
      }
    }

    // LEFT
    b.breakable {
      for(i <- 1 to m("fire")) {
        if(stage.stage_objects_map(nindex - i) == '2') b.break
        else if(stage.stage_objects_map(nindex - i) == '3') {
          stage.stage_objects_map(nindex - i) = stage.stage_objects_map_original(nindex - i)
          b.break
        }
        else {
          stage.stage_objects_map(nindex - i) = '4'
        }
      }
    }

    // RIGHT
    b.breakable {
      for(i <- 1 to m("fire")) {
        if(stage.stage_objects_map(nindex + i) == '2') b.break
        else if(stage.stage_objects_map(nindex + i) == '3') {
          stage.stage_objects_map(nindex + i) = stage.stage_objects_map_original(nindex + i)
          b.break
        }
        else {
          stage.stage_objects_map(nindex + i) = '4'
        }
      }
    }

  }
}
