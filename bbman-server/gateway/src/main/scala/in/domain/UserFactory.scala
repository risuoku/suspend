package in.domain

import in.domain.{User, UserImpl}

trait UserFactory {
  def create(identity: Int, src_addr: String, username: String, nickname: String): User
}

object UserFactory {
  def create(identity: Int, src_addr: String, username: String, nickname: String): User = {
    try {
      new UserImpl(
        identity, 
        src_addr,
        username,
        nickname
      )
    }
  }
}
