package in.infrastructure

import java.util.concurrent.ArrayBlockingQueue

import in.Config

object SendMessageQueueOnMemory {
  val smq = new ArrayBlockingQueue[String](Config.getInt("mqsize"))

  def isEmpty() = {smq.size() == 0}

  def push(s: String) = smq.put(s)

  def pop() = smq.poll()
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
