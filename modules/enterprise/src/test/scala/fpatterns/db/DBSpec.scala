package fpatterns.db

import domain.Domain
import org.scalatest.WordSpec
import org.scalatest.matchers.MustMatchers
import fpatterns.Success

final class DBSpec
    extends MustMatchers
    with WordSpec
    with ConnectionProviders
    with SQLScripts
    with Domain
    with DBs {

  private def provider = makeUnManagedProvider("org.h2.Driver", "jdbc:h2:memtest")

  def createNewUser(login: String, password: String): DB[Option[User]] =
    for {
      _ <- createTableUser
      u <- createUser(login, password)
      _ <- dropTableUser
    } yield u

  "Get User" must {
    "find a user when created" in {
      provider(createNewUser("rabbit", "carott")) must be(Success(Some(User("rabbit", "carott"))))
    }
  }
}
