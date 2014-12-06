package in.infrastructure.iothread

import in.Config
import java.io._
import java.net._

object IOResource {
  val port = Config.getInt("port.udp.server")
  val socket: DatagramSocket = new DatagramSocket(port)
}
