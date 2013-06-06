package fpatterns.db

import features.{ DomainValidations, Persistence, Domain, SQLScripts }
import fpatterns.{ Monads, Kleislis }
import fpatterns.validation._

trait PersistenceContextFixture extends ConnectionProviders
    with PersistenceContext
    with SQLScripts
    with Domain
    with Persistence
    with DomainValidations
    with Kleislis
    with Monads {

  override protected def provider = makeUnManagedProvider("org.h2.Driver", "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")

  def insertUser(user: User): DBResult[Int] = user >=: (action(validateUser) >=> createUser)

  def findUser(login: String): DBResult[Option[User]] = login >=: readUser

  def getUser(login: String): DBResult[User] = login >=: (readUser map (expect[String, User]("User expected")))

  def insertAddress(user: User)(address: Address): DBResult[Int] = address >=: (action(validateAddress) >=> createAddress(user))

  def findAddress(user: User): DBResult[Option[Address]] = user >=: readAddress

  def getAddress(user: User): DBResult[Address] = user >=: (readAddress map (expect[User, Address]("Address expected")))

  def createDomain(user: User): DBResult[Option[User]] =
    for {
      _ <- insertUser(user)
      u <- findUser(user.login)
    } yield u

  def createSchema: DBResult[Unit] = for {
    _ <- createTableUser
    _ <- createTableAddress
  } yield ()

  def dropSchema: DBResult[Unit] = for {
    _ <- dropTableAddress
    _ <- dropTableUser
  } yield ()

  def createDomains(user: User, address: Address): DBResult[(User, Address)] =
    for {
      _ <- insertUser(user)
      u <- getUser(user.login)
      _ <- insertAddress(u)(address)
      a <- getAddress(u)
    } yield (u, a)

}
