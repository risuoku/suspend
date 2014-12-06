package in.infrastructure

import java.util.concurrent.ArrayBlockingQueue

import in.Config

object SendMessageQueueOnMemory {
  val smq = new ArrayBlockingQueue[String](Config.getInt("mqsize"))

  def isEmpty() = {smq.size() == 0}

  def push(s: String) = smq.put(s)

  def pop() = smq.poll()

  def size = smq.size()
}

class SingleBattleRecieveMessageQueueOnMemory {
  val smq = new ArrayBlockingQueue[String](Config.getInt("mqsize"))

  def isEmpty() = {smq.size() == 0}

  def push(s: String) = smq.put(s)

  def pop() = smq.poll()
}

object BattleRecieveMessageQueueOnMemory {
  val queues: Map[Int, SingleBattleRecieveMessageQueueOnMemory] = (1 to Config.getInt("battle_thread_size")).toList.map (
    n => (n, new SingleBattleRecieveMessageQueueOnMemory)
  ).toMap
}

object KeepaliveRecieveMessageQueueOnMemory {
  val smq = new ArrayBlockingQueue[String](Config.getInt("mqsize"))

  def isEmpty() = {smq.size() == 0}

  def push(s: String) = smq.put(s)

  def pop() = smq.poll()
}

object KeepaliveSendMessageQueueOnMemory {
  val smq = new ArrayBlockingQueue[String](Config.getInt("mqsize"))

  def isEmpty() = {smq.size() == 0}

  def push(s: String) = smq.put(s)

  def pop() = smq.poll()
}
