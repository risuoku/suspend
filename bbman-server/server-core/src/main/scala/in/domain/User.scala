package in.domain

import in.Config

trait User {
  def identity: Int
  def src_addr: String
  def username: String
  def nickname: String
  def point: Int
  def room: Int

  override def toString: String

  case class ExternalUser (
    id: Int,
    nickname: String,
    point: Int,
    room: Int
  )
  def toExternalUser: ExternalUser
}

class UserImmutableOnMemory (
    _identity: Int,
    _src_addr: String,
    _username: String,
    _nickname: String,
    _point: Int,
    _room: Int
  ) extends User {

  def identity = _identity
  def src_addr = _src_addr
  def username = _username
  def nickname = _nickname
  def point = _point
  def room = _room

  override def toString = {
    "identity: %d, src_addr: %s, username: %s, nickname: %s, point: %d".format(identity, src_addr, username, nickname, point)
  }

  def toExternalUser: ExternalUser = {
    ExternalUser (
      identity,
      nickname,
      point,
      room
    )
  }
}

class UserOnMemory (
    _identity: Int,
    _src_addr: String,
    _username: String,
    _nickname: String,
    _point: Int,
    _room: Int
  ) extends User {

  private var __identity: Int = _identity
  private var __src_addr: String = _src_addr
  private var __username: String = _username
  private var __nickname: String = _nickname
  private var __point: Int = _point
  private var __room: Int = _room
  
  def identity = __identity
  def src_addr = __src_addr
  def username = __username
  def nickname = __nickname
  def point = __point
  def room = __room

  override def toString = {
    "identity: %d, src_addr: %s, username: %s, nickname: %s, point: %d".format(identity, src_addr, username, nickname, point)
  }

  def toExternalUser: ExternalUser = {
    ExternalUser (
      identity,
      nickname,
      point,
      room
    )
  }
}
