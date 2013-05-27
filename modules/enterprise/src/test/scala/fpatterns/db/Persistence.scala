package fpatterns.db

import domain.Domain
import fpatterns._
import fpatterns.validation._

protected[db] trait Persistence {
  self: DBActions with SQLScripts with Domain =>
  protected[db] def createTableUser: DBResult[Unit] =
    DBResult[Unit] { connection => safely { closing(connection.createStatement())(_.execute(createUserTable)) } }

  protected[db] def dropTableUser: DBResult[Unit] =
    DBResult[Unit] { connection => safely { closing(connection.createStatement())(_.execute(dropUserTable)) } }

  protected[db] def createUser: DBAction[User, Int] = DBAction[User, Int] { connection =>
    (user: User) => safely {
      closing(connection.prepareStatement(insertUserRecord)) { statement =>
        statement.setString(1, user.login)
        statement.setString(2, user.password)
        statement.executeUpdate()
      }
    }
  }

  protected[db] def readUser: DBAction[String, Option[User]] = DBAction[String, Option[User]] { connection =>
    (login: String) => safely {
      closing(connection.prepareStatement(selectUserRecord)) { statement =>
        statement.setString(1, login)
        closing(statement.executeQuery()) { rs =>
          rs.next() match {
            case true => Some(User(rs.getString("LOGIN"), rs.getString("PASSWORD")))
            case _    => None
          }
        }
      }
    }
  }
}
