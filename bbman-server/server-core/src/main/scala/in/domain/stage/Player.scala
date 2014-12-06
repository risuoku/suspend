package in.domain.stage

import scala.collection.mutable.HashMap
import in.domain.Stage

class Player(user_id: Int, _user_name: String, _src_addr: String, _xx: Int, _yy: Int, _stage: Stage) {
  val m = HashMap[String, Int] (
    "xx" -> _xx,
    "yy" -> _yy,
    "user_id" -> user_id,
    "obj" -> 49,
    "life" -> 1,
    "bomb" -> 1,
    "fire" -> 6,
    "speed" -> 1,
    "kick" -> 0,
    "panch" -> 0,
    "throwing" -> 0,
    "direction" -> 0
  )
  val src_addr = _src_addr
  val user_name = _user_name
  val xx = _xx
  val yy = _yy

  val stage = _stage
  //val MOVE_PER_CLIENT_KEYEVENT = 8000
  val MOVE_PER_CLIENT_KEYEVENT = 4000
  
  /*
    通常ここはシンボルで済ませるが、ネットワークでシンボルを渡せないので
    Int の形式で疑似的にシンボルを設定している
  */
  val (_UP, _DOWN, _LEFT, _RIGHT, _GROB, _BOMBSETorTHROW) = (1, 2, 3, 4, 5, 6)

  def set_coordinate(xx: Int, yy: Int): Unit = {
    m("xx") += xx
    m("yy") += yy
  }

  def set_obj(obj: Int): Unit = {
    m("obj") = obj
  }

  def init_player() = {
    m("xx") = xx; m("yy") = yy
    m("life") = 1
    m("bomb") = 1
    m("fire") = 6
    m("speed") = 1
    m("kick") = 0
    m("panch") = 0
    m("throwing") = 0
    m("direction") = 0
  }

  def countup_bomb() = {
    m("bomb") += 1
  }

  def countdown_bomb() = {
    m("bomb") -= 1
  }

  def get_state(): HashMap[String, Int] = {
    m
  }

  def get_external_state() = {
    Map[String, Int] (
      "xx" -> m("xx")/stage.INTERNAL_PER_PIX,
      "yy" -> m("yy")/stage.INTERNAL_PER_PIX,
      "obj" -> m("obj"),
      "user_id" -> m("user_id"),
      "life" -> m("life"),
      "bomb" -> m("bomb"),
      "fire" -> m("fire"),
      "speed" -> m("speed"),
      "kick" -> m("kick"),
      "panch" -> m("panch"),
      "throwing" -> m("throwing"),
      "direction" -> m("direction")
    )
  }

  def handle_client_keyevent(s: Int) = {
    s match {
      case `_UP` => {_self_move('UP); m("direction") = 2}
      case `_DOWN` => {_self_move('DOWN); m("direction") = 0}
      case `_LEFT` => {_self_move('LEFT); m("direction") = 1}
      case `_RIGHT` => {_self_move('RIGHT); m("direction") = 3}
      case `_GROB` => _grab_action()
      case `_BOMBSETorTHROW` => _bombset_or_throwing()
      case _ => {println("println")}
    }
  }

  private def _self_move(s: Symbol) = {
    val center_x = m("xx") + stage.INTERNAL_PER_CELL / 2
    val center_y = m("yy") + stage.INTERNAL_PER_CELL / 2
    s match {
      case 'UP|'DOWN => {
        val nindex = stage.internal_coordinate2external_som_index (
          center_x, s match {
            case 'UP => center_y - stage.INTERNAL_PER_CELL
            case 'DOWN => center_y + stage.INTERNAL_PER_CELL
          } 
        )
        if(_is_simple_coordinate(m("xx"))) { 
          _self_simple_move(s)
        } else if(_blocked_by_blocks(nindex) || _blocked_by_bombs(nindex)) { // 隣がブロックの場合
          if(center_x > stage.round_down_internal(center_x) + stage.INTERNAL_PER_CELL / 2) {
            _self_simple_move('RIGHT)
          } else {
            _self_simple_move('LEFT)
          }
        } else {
          if(center_x > stage.round_down_internal(center_x) + stage.INTERNAL_PER_CELL / 2) {
            _self_simple_move('LEFT)
          } else {
            _self_simple_move('RIGHT)
          }
        }
      }
      case 'RIGHT|'LEFT =>  { 
        val nindex = stage.internal_coordinate2external_som_index (
          s match {
            case 'RIGHT => center_x + stage.INTERNAL_PER_CELL
            case 'LEFT => center_x - stage.INTERNAL_PER_CELL
          } ,center_y
        )
        if(_is_simple_coordinate(m("yy"))) { 
          _self_simple_move(s)
        } else if(_blocked_by_blocks(nindex) || _blocked_by_bombs(nindex)) { // 隣がブロックの場合
          if(center_y > stage.round_down_internal(center_y) + stage.INTERNAL_PER_CELL / 2) {
            _self_simple_move('DOWN)
          } else {
            _self_simple_move('UP)
          }
        } else {
          if(center_y > stage.round_down_internal(center_y) + stage.INTERNAL_PER_CELL / 2) {
            _self_simple_move('UP)
          } else {
            _self_simple_move('DOWN)
          }
        }
      }
    }
  }

  private def _self_simple_move(s: Symbol) = {
    s match {
      case 'UP => {
        val nindex = stage.internal_coordinate2external_som_index (
          m("xx"), m("yy") - MOVE_PER_CLIENT_KEYEVENT
        )
        if(_blocked_by_blocks(nindex) || _blocked_by_bombs(nindex)) {
          m("yy") = stage.round_down_internal(m("yy") - MOVE_PER_CLIENT_KEYEVENT) + stage.INTERNAL_PER_CELL
        } else {
          m("yy") -= MOVE_PER_CLIENT_KEYEVENT
        }
      }
      case 'DOWN => {
        val nindex = stage.internal_coordinate2external_som_index (
          m("xx"), m("yy") + stage.INTERNAL_PER_CELL + MOVE_PER_CLIENT_KEYEVENT
        )
        if(_blocked_by_blocks(nindex) || _blocked_by_bombs(nindex)) {
          m("yy") = stage.round_down_internal(m("yy") + MOVE_PER_CLIENT_KEYEVENT)
        } else {
          m("yy") += MOVE_PER_CLIENT_KEYEVENT
        }
      }
      case 'LEFT => {
        val nindex = stage.internal_coordinate2external_som_index (
          m("xx") - MOVE_PER_CLIENT_KEYEVENT, m("yy")
        )
        if(_blocked_by_blocks(nindex) || _blocked_by_bombs(nindex)) {
          m("xx") = stage.round_down_internal(m("xx") - MOVE_PER_CLIENT_KEYEVENT) + stage.INTERNAL_PER_CELL
        } else {
          m("xx") -= MOVE_PER_CLIENT_KEYEVENT
        }
      }
      case 'RIGHT => {
        val nindex = stage.internal_coordinate2external_som_index (
          m("xx") + stage.INTERNAL_PER_CELL + MOVE_PER_CLIENT_KEYEVENT, m("yy")
        )
        if(_blocked_by_blocks(nindex) || _blocked_by_bombs(nindex)) {
          m("xx") = stage.round_down_internal(m("xx") + MOVE_PER_CLIENT_KEYEVENT)
        } else {
          m("xx") += MOVE_PER_CLIENT_KEYEVENT
        }
      }
    }
  }

  private def _is_simple_coordinate(n: Int) = {
    n % stage.INTERNAL_PER_CELL == 0
  }

  private def _blocked_by_blocks(nindex: Int) = {
    stage.stage_objects_map(nindex) == '2' || stage.stage_objects_map(nindex) == '3' 
  }

  private def _blocked_by_bombs(nindex: Int) = {
    val nindex_p = stage.internal_coordinate2external_som_index(
      m("xx") + stage.INTERNAL_PER_CELL / 2,
      m("yy") + stage.INTERNAL_PER_CELL / 2
    )
    stage.bombs_map(nindex) == 1 && nindex_p != nindex
  }

  private def _grab_action() = {}

  private def _bombset_or_throwing() = {
    stage.bombset_or_throwing(m("xx"), m("yy"), m("user_id"), m("fire"))
  }

  def check_alival() = {
    val center_x = m("xx") + stage.INTERNAL_PER_CELL / 2
    val center_y = m("yy") + stage.INTERNAL_PER_CELL / 2

    val nindex = stage.internal_coordinate2external_som_index (
      center_x, center_y
    )

    stage.stage_objects_map(nindex) match {
      case '4'|'5'|'6'|'7'|'8'|'9'|'a' => {m("life") -= 1}
      case _ => {}
    }

    m("life") > 0
  }
}
