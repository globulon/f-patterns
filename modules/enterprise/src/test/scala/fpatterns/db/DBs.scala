package fpatterns.db

import domain.Domain
import fpatterns._

protected[db] trait DBs {
  self: SQLScripts with Domain =>
  protected[db] def createTableUser: DB[Unit] = DB {
    connection =>
      {
        closing(connection.createStatement()) { statement =>
          statement.execute(createUserTable)
        }
      }
  }

  protected[db] def dropTableUser: DB[Unit] = DB[Unit] {
    connection =>
      {
        closing(connection.createStatement()) { statement =>
          statement.execute(dropUserTable)
        }
      }
  }

  protected[db] def insertUser(login: String, password: String): DB[Unit] = DB[Unit] {
    connection =>
      {
        closing(connection.prepareStatement(insertUserRecord)) { statement =>
          statement.setString(1, login)
          statement.setString(2, password)
          statement.executeUpdate()
        }
      }
  }

  protected[db] def getUser(login: String): DB[Option[User]] = DB[Option[User]] {
    connection =>
      {
        closing(connection.prepareStatement(selectUserRecord)) { statement =>
          statement.setString(1, login)
          val rs = statement.executeQuery()
          rs.next() match {
            case true => Some(User(rs.getString("LOGIN"), rs.getString("PASSWORD")))
            case _    => None
          }
        }
      }
  }

  protected[db] def createUser(login: String, password: String): DB[Option[User]] =
    for {
      _ <- insertUser(login, password)
      u <- getUser(login)
    } yield u
}
