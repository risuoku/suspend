package in.infrastructure.iothread

import java.io._
import java.net._

import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.json4s.JsonDSL._

import in.Config
import in.infrastructure._
import in.infrastructure.iothread.IOResource
import in.infrastructure.json.RecieveJsonController

object RecieverThread extends Thread {
  val buf: Array[Byte] = new Array(256)
  val packet: DatagramPacket = new DatagramPacket(buf, buf.length)

  var running = true

  override def run() = {
    while(running) {
      IOResource.socket.receive(packet)
      val src_addr: String = packet.getAddress().toString.replaceAll("/", "")
      val len: Int = packet.getLength()
      val msg: String = new String(buf, 0, len, "MS932")
      _handleMessage(msg, src_addr)
    }
  }

  private def _handleMessage(msg: String, src_addr: String) = {
    val target = RecieveJsonController.extractTarget(msg)
    target match {
      case 0 => KeepaliveRecieveMessageQueueOnMemory.push(msg)
      case _ => throw new RuntimeException("hogefuga")
    }
  }
}
