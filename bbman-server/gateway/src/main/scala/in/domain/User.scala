package in.domain

import in.Config

trait User {
  val identity: Int
  val src_addr: String
  val username: String
  val nickname: String
  val room: Int

  override def toString: String
}

class UserImpl (
    _identity: Int,
    _src_addr: String,
    _username: String,
    _nickname: String
  ) extends User {

  val identity = _identity
  val src_addr = _src_addr
  val username = _username
  val nickname = _nickname
  val room = -2

  override def toString = {
    "identity: %d, src_addr: %s, username: %s, nickname: %s".format(identity, src_addr, username, nickname)
  }
}
