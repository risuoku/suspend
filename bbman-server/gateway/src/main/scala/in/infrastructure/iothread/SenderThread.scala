package in.infrastructure.iothread

import java.io._
import java.net._

import in.Config
import in.infrastructure.SendMessageQueueOnMemory
import in.infrastructure.json.SendJsonController
import in.infrastructure.iothread.IOResource

object SenderThread extends Thread {
  val port = Config.getInt("port.udp.client")
  val smq = SendMessageQueueOnMemory

  var running = true

  override def run() = {
    while(running) {
      while(! smq.isEmpty()) {
        val msg = smq.pop()
        println("send" + msg)
        val inet: InetAddress = InetAddress.getByName(SendJsonController.extractSrcAddr(msg))
        val buf: Array[Byte] = msg.getBytes("MS932")
        val packet = new DatagramPacket(buf, buf.length, inet, port)
        IOResource.socket.send(packet)
      }
      Thread.sleep(50)
    }
  }
}
