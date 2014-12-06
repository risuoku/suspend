package in.infrastructure.iothread

import java.io._
import java.net._

import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.json4s.JsonDSL._

import in.Config
import in.infrastructure.json.RecieveJsonController

object SyncMessageServer {
  val port = Config.getInt("port.tcp.server")
  val server = new ServerSocket()
  server.setReuseAddress(true)
  server.bind(new InetSocketAddress(port))

  var _running = true

  def start(func: String => String) = {
    while(_running) {
      val socket: Socket = server.accept()
      (new EventHandler(socket, func)).start()
    }
  }
}

class EventHandler(socket: Socket, func: String => String) extends Thread {
  override def run() = {
    val in = new BufferedInputStream(socket.getInputStream())
    val out = new BufferedOutputStream(socket.getOutputStream())
    val src_addr = socket.getInetAddress().toString.replaceAll("/", "")

    val buf: Array[Byte] = new Array(256)
    var count = -1

    count = in.read(buf)

    // get raw msg
    val msg = new String(buf, 0, count, "MS932")
    
    // get formatted msg and push to client
    val msg_r = func (
      _forwardMsg(msg, src_addr)
    )

    val buf_r = msg_r.getBytes("MS932")
    println(msg_r)
    out.write(buf_r, 0, buf_r.length)
    out.flush()
  }

  private def _forwardMsg(msg: String, src_addr: String): String = {
    val target = RecieveJsonController.extractTarget(msg)
    target match {
      case 1 => _addSrcAddr(msg, src_addr)
    }
  }

  private def _addSrcAddr(msg: String, src_addr: String): String = {
    compact(render(
      Map[String, String] (
        "original" -> msg,
        "src_addr" -> src_addr
      )
    ))
  }
}
