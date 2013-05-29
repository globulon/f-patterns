package fpatterns.db

import domain.Domain
import fpatterns.{ Failure, Kleislis, Success }
import org.scalatest.WordSpec
import org.scalatest.matchers.MustMatchers

final class DBSpec
    extends MustMatchers
    with WordSpec
    with ConnectionProviders
    with SQLScripts
    with Domain
    with DBActions
    with Persistence
    with DomainValidations
    with Kleislis {

  private def provider = makeUnManagedProvider("org.h2.Driver", "jdbc:h2:mem:test")

  def insertUser(user: User): DBResult[Int] =
    user >=: (action(validateUser) >=> createUser)

  def getUser(login: String): DBResult[Option[User]] = login >=: readUser

  def createDomain(user: User): DBResult[Option[User]] =
    for {
      _ <- createTableUser
      _ <- insertUser(user)
      u <- getUser(user.login)
      _ <- dropTableUser
    } yield u

  "Create new user" must {
    "insert a valid user" in {
      provider(createDomain(User("rabbit", "car0tt"))) must be(Success(Some(User("rabbit", "car0tt"))))
    }

    "not insert an valid user" in {
      provider(createDomain(User("rabbit", "carott"))) must be(Failure("password must contain at least character with 1 digit "))
    }
  }

}
