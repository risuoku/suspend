package in.domain

import org.specs2.mutable._
import org.specs2.mock.Mockito

import scala.collection.mutable.HashMap

import in.domain.{User, UserFactory}
import in.Config

object UserRepositoryOnMemorySpec extends Specification with Mockito {

  sequential

  "UserRepositoryOnMemory#authenticate" should {

    "return 123 for testman1" in {
      UserRepositoryOnMemory.authenticate("testman1", "password", "localhost").identity must be_==(123)
    }
    "return testman1 for testman1" in {
      UserRepositoryOnMemory.authenticate("testman1", "password", "localhost").username must be_==("testman1")
    }
    "return risuosan for testman1" in {
      UserRepositoryOnMemory.authenticate("testman1", "password", "localhost").nickname must be_==("risuosan")
    }

    "return 234 for testman2" in {
      UserRepositoryOnMemory.authenticate("testman2", "password", "localhost").identity must be_==(234)
    }
    "return testman2 for testman2" in {
      UserRepositoryOnMemory.authenticate("testman2", "password", "localhost").username must be_==("testman2")
    }
    "return ogamansan for testman2" in {
      UserRepositoryOnMemory.authenticate("testman2", "password", "localhost").nickname must be_==("ogamansan")
    }
  }

  "UserRepositoryOnMemory#setUser" should {
    "加えたユーザが正しく反映されている" in new context2 {
      UserRepositoryOnMemory.setUser(m1)
      UserRepositoryOnMemory.getUserById(m1.identity).identity must be_==(m1.identity)
    }
    "ユーザの更新が正しく反映されている for username" in new context2 {
      UserRepositoryOnMemory.setUser(m1)
      UserRepositoryOnMemory.setUser(m2)
      UserRepositoryOnMemory.getUserById(m1.identity).username must be_==(m2.username)
    }
    "ユーザの更新が正しく反映されている for nickname" in new context2 {
      UserRepositoryOnMemory.setUser(m1)
      UserRepositoryOnMemory.setUser(m2)
      UserRepositoryOnMemory.getUserById(m1.identity).nickname must be_==(m2.nickname)
    }
  }

  "UserRepositoryOnMemory#removeUserById" should {

    "ユーザの削除が正しく反映されている for getUserByIdでException" in new context1 {
      UserRepositoryOnMemory.setUser(m)
      UserRepositoryOnMemory.removeUserById(m.identity)
      UserRepositoryOnMemory.getUserById(m.identity) must throwA[java.util.NoSuchElementException]
    }
    "ユーザの削除が正しく反映されている for isAliveByIdでException" in new context1 {
      UserRepositoryOnMemory.setUser(m)
      UserRepositoryOnMemory.removeUserById(m.identity)
      UserRepositoryOnMemory.isAliveById(m.identity) must throwA[java.util.NoSuchElementException]
    }
  }

  // contexts
  trait context1 extends Before {
    val m = mock[in.domain.User]

    def before = {
      m.identity returns 123
      m.src_addr returns "127.0.0.1"
      m.username returns "testman1"
      m.nickname returns "risuosan"
      m.point returns 4321
    }
  }

  trait context2 extends Before {
    val m1 = mock[in.domain.User]
    val m2 = mock[in.domain.User]

    def before = {
      m1.identity returns 123
      m1.src_addr returns "127.0.0.1"
      m1.username returns "testman1"
      m1.nickname returns "risuosan"
      m1.point returns 4321

      m2.identity returns 123
      m2.src_addr returns "127.0.0.1"
      m2.username returns "testman3"
      m2.nickname returns "moritaman"
      m2.point returns 1234
    }
  }
}
