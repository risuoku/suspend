package in.domain

import scala.collection.mutable.HashMap

import in.domain.{User, UserFactory}
import in.Config

trait UserRepository {
  def authenticate(username: String, password: String, src_addr: String): User 
  def setUser(u: User): Unit
  def getUserById(id: Int): User
  def getUserList: List[User]
  def removeUserById(id: Int): Option[User]
  def initKeepalivecountById(id: Int): Unit
  def incrementKeepalivecountById(id: Int): Unit
  def isAliveById(id: Int): Boolean
}

object UserRepositoryOnMemory extends UserRepository {
  val dummyUserContainer: List[(String, String, Int, String, Int)] = List(
    ("risuosan", "password", 1, "risuo", 7777),
    ("Scaled_Wurm", "password", 2, "ogamansan", 5432),
    ("mah884", "password", 3, "mah884", 5432),
    ("kiyukuta", "password", 4, "kiyukuta", 5432),
    ("tanpopo", "password", 5, "蒲公英", 5432)
  )

  val users = new HashMap[Int, User]()
  val keepalivecount = new HashMap[Int, Int]()

  val count_keepalive = Config.getInt("count.keepalive")

  def authenticate(username: String, password: String, src_addr: String): User = {
    // dummyUserContainer は、後々永続化用のinfrastructureに置き換わる
    val r = dummyUserContainer.filter(
      t => (t._1 == username && t._2 == password)
    )
    r.size match {
      case 1 => UserFactory.create (
        r(0)._3,
        src_addr,
        r(0)._1,
        r(0)._4,
        r(0)._5,
        -1
      )
      case _ => throw new RuntimeException("hogefuga")
    }
  }

  def setUser(u: User): Unit = {
    users(u.identity) = u
    keepalivecount += (u.identity -> 0)
  }

  def getUserById(id: Int): User = {
    users(id)
  }
  
  def getUserList: List[User] = {
    users.toList.map (
      u => u._2
    )
  }

  def getExternalUserList = {
    getUserList.map (
      u => u.toExternalUser
    )
  }

  def removeUserById(id: Int): Option[User] = {
    keepalivecount.remove(id)
    users.remove(id)
  }

  def initKeepalivecountById(id: Int): Unit = {
    keepalivecount(id) = 0
  }

  def incrementKeepalivecountById(id: Int): Unit = {
    keepalivecount(id) += 1
  }

  def isAliveById(id: Int): Boolean = {
    keepalivecount(id) < count_keepalive
  }

  def debugprint() = {
    users.foreach(u => println(u._2)) 
    keepalivecount.foreach(k => println(k._2))
  }
}
