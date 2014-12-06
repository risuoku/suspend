package in

import com.typesafe.config.ConfigFactory

object Config {
  val conf = ConfigFactory.load()

  def getInt(s: String) = conf.getInt(s)

  def getString(s: String) = conf.getString(s)
}
