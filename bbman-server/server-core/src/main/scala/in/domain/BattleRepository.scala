package in.domain

import scala.collection.mutable.HashMap

import in.domain.{Battle, BattleFactory}
import in.Config

trait BattleRepository {
  def startById(identity: Int)
  def shutdownById(identity: Int)
}

object BattleRepositoryOnMemory extends BattleRepository {

  val battles = new HashMap[Int, Battle]()

  def startById(identity: Int) = {
    val b = BattleFactory.create(identity)
    battles(identity) = b
    battles(identity).start()
  }

  def shutdownById(identity: Int) = {
    println("battle shutdown!!!")
    battles(identity).shutdown()
    Thread.sleep(2000)
    battles.remove(identity)
  }

  def getBattleById(identity: Int) = {
    battles(identity)
  }
}
