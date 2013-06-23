package fpatterns.db

import fpatterns.async.{ ProvideContext, AsyncContext }
import fpatterns.test.helpers.AsyncAssertions
import org.scalatest.matchers.MustMatchers
import org.scalatest.{ BeforeAndAfterAll, WordSpec }
import scala.concurrent.duration._
import scala.language.postfixOps

final class AsyncPersistenceContextSpec extends MustMatchers
    with WordSpec
    with PersistenceContextFixture
    with AsyncPersistenceContext
    with AsyncContext
    with AsyncAssertions
    with BeforeAndAfterAll {

  override protected def provideConnection = makeUnManagedProvider("org.h2.Driver", "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")

  def provideContext = ProvideContext(scala.concurrent.ExecutionContext.global)

  def defaultDuration = 1 second

  "Create new user" must {
    "insert a valid user" in {

      checkAsyncSuccess(pTransact(createDomain(User(0, "Thumper", "car0tt")))) { user =>
          user map (_.login) must not(be(0))
          user map (_.login) must be(Some("Thumper"))
          user map (_.password) must be(Some("car0tt"))
      }

      checkAsyncSuccess(pReadOnly(findUser("Thumper")))(_ must not(be(None)))
    }

    "not insert an invalid user" in {
      checkFailure(transact(createDomain(User(0, "rabbit", "carott")))) {
        _.mkString must include("password must contain at least 6 character with 1 digit ")
      }
    }
  }

  "Create user and Address" must {
    "create both entities" in {
      checkAsyncSuccess(pTransact(createDomains(User(0, "bambi", "fl0wer"), Address(0, "woods alley", 7)))) {
        case (user, address) =>
          user.login must not(be(0))
          user.login must be("bambi")
          user.password must be("fl0wer")

          address.id must not(be(0))
          address.street must be("woods alley")
          address.number must be(7)
      }

      checkAsyncSuccess(pReadOnly(findUser("bambi"))) { bambi =>
        bambi must not(be(None))
        bambi foreach { deer =>
          checkSuccess(readOnly(findAddress(deer)))(_ must not(be(None)))
        }
      }
    }

    "rollback all" in {
      checkAsyncFailure(pTransact(createDomains(User(0, "Flower", "bamb1no"), Address(0, "woods alley", -1)))) {
        _.mkString must include("Address street number must be greater than 0")
      }

      checkAsyncSuccess(pReadOnly(findUser("Flower")))(_ must be(None))
    }
  }

  override protected def beforeAll() {
    checkAsyncSuccess(pTransact(createSchema)) { _ => () }
  }

  override protected def afterAll() {
    checkAsyncSuccess(pTransact(dropSchema)) { _ => () }
  }
}
