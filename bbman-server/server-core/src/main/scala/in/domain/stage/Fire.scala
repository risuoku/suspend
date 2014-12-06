package in.domain.stage

import scala.collection.mutable.HashMap

class Fire(nindex: Int) {
  val m = HashMap[String, Int] (
    "nindex" -> nindex,
    "count" -> 0
  )
}
