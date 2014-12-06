package in.domain.json

import org.specs2.mutable._
import org.specs2.mock.Mockito

import in.domain.json.{AuthenticationJsonController}

/*
  Authentication Json Parameters
*/


object AuthenticationJsonControllerSpec extends Specification with Mockito {
  
  sequential

  "AuthenticationJsonControllerSpec#createSuccessMsg" should {

    "SuccessMsg が正しく生成されている" in new Context1 {
      json_c.createSuccessMsg("127.0.0.1", 432) must be_==(msg1)
    }
  }

  trait Context1 extends BeforeAfter {
    val json_c = AuthenticationJsonController
    val msg1 = "{\"target\":1,\"src_addr\":\"127.0.0.1\",\"id\":432}"

    def before = {
    }
    def after = {}
  }
}
