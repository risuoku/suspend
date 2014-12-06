package in.domain

import in.domain.{Battle}

trait BattleFactory {
  def create(identity: Int): Battle
}

object BattleFactory {
  def create(identity: Int): Battle = {
    try {
      new Battle (
        identity 
      )
    }
  }
}
