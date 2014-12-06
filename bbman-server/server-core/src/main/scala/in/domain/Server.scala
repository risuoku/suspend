package in.domain

trait Server {
  val identity: Int
  val name: String
  def getPlayerNum(): Int
}
