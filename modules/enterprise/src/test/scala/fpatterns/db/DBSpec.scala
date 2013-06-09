package fpatterns.db

import fpatterns.test.helpers.Assertions
import org.scalatest.{ BeforeAndAfterAll, WordSpec }
import org.scalatest.matchers.MustMatchers

final class DBSpec extends MustMatchers
    with WordSpec
    with PersistenceContextFixture
    with Assertions
    with BeforeAndAfterAll {

  "Create new user" must {
    "insert a valid user" in {
      checkSuccess(inTx(createDomain(User(0, "Thumper", "car0tt")))) { user =>
        user map (_.login) must not(be(0))
        user map (_.login) must be(Some("Thumper"))
        user map (_.password) must be(Some("car0tt"))
      }

      checkSuccess(readOnly(findUser("Thumper")))(_ must (not(be(None))))
    }

    "not insert an invalid user" in {
      checkFailure(inTx(createDomain(User(0, "rabbit", "carott")))) {
        _.mkString must include("password must contain at least character with 1 digit ")
      }
    }
  }

  "Create user and Address" must {
    "create both entities" in {
      checkSuccess(inTx(createDomains(User(0, "bambi", "fl0wer"), Address(0, "woods alley", 7)))) {
        case (user, address) =>
          user.login must not(be(0))
          user.login must be("bambi")
          user.password must be("fl0wer")

          address.id must not(be(0))
          address.street must be("woods alley")
          address.number must be(7)
      }

      checkSuccess(readOnly(findUser("bambi"))) { bambi =>
        bambi must (not(be(None)))
        bambi foreach { deer =>
          checkSuccess(readOnly(findAddress(deer)))(_ must (not(be(None))))
        }
      }
    }

    "rollback all" in {
      checkFailure(inTx(createDomains(User(0, "Flower", "bamb1no"), Address(0, "woods alley", -1)))) {
        _.mkString must include("Address street number must be greater than 0")
      }

      checkSuccess(readOnly(findUser("Flower")))(_ must (be(None)))
    }
  }

  override protected def beforeAll() {
    checkSuccess(inTx(createSchema)) { _ => () }
  }

  override protected def afterAll() {
    checkSuccess(inTx(dropSchema)) { _ => () }
  }
}
