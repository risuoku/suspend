package in.domain

//import in.domain.{User, UserImmutableOnMemory}
import in.domain.{User}

trait UserFactory {
  def create(identity: Int, src_addr: String, username: String, nickname: String, point: Int, room: Int): User
}

object UserFactory {
  def create(identity: Int, 
             src_addr: String,
             username: String,
             nickname: String,
             point: Int,
             room: Int): User = {
    try {
      new UserImmutableOnMemory(
        identity, 
        src_addr,
        username,
        nickname,
        point,
        room
      )
    }
  }
}
