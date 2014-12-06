package in.domain.json

import org.specs2.mutable._
import org.specs2.mock.Mockito

import in.domain.json.{KeepaliveJsonController}

/*
  Keepalive Json Parameters

  target: Int
  id: Int
*/


object KeepaliveJsonControllerSpec extends Specification with Mockito {
  
  sequential

  "KeepaliveJsonControllerSpec#extractIdentity" should {

    "identity が正しく取得されている" in new Context1 {
      json_c.extractIdentity(msg1) must be_==(123)
    }
  }

  "KeepaliveJsonControllerSpec#create_keepalive_msg" should {

    "keepalive_msg が正しく生成されている" in new Context2 {
      json_c.create_keepalive_msg(m1) must be_==(msg2)
    }
  }

  // contexts
  trait Context1 extends BeforeAfter {
    val json_c = KeepaliveJsonController
    val msg1 = "{\"target\":0, \"id\":123}"

    def before = {}
    def after = {}
  }

  trait Context2 extends BeforeAfter {
    val json_c = KeepaliveJsonController
    val msg2 = "{\"target\":0, \"src_addr\":\"127.0.0.1\",\"room\":-2}"
    val m1 = mock[in.domain.User]

    def before = {
      m1.src_addr returns "127.0.0.1"
      m1.room returns -2
    }
    def after = {}
  }
}
