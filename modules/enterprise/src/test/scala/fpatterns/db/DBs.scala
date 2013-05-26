package fpatterns.db

import domain.Domain
import fpatterns._

protected[db] trait DBs {
  self: SQLScripts with Domain =>
  protected[db] def createTableUser: DB[Unit] = DB { connection =>
    safely {
      closing(connection.createStatement())(_.execute(createUserTable))
    }
  }

  protected[db] def dropTableUser: DB[Unit] = DB[Unit] { connection =>
    safely {
      closing(connection.createStatement())(_.execute(dropUserTable))
    }
  }

  protected[db] def insertUser(login: String, password: String): DB[Unit] = DB[Unit] { connection =>
    safely {
      closing(connection.prepareStatement(insertUserRecord)) { statement =>
        statement.setString(1, login)
        statement.setString(2, password)
        statement.executeUpdate()
      }
    }
  }

  protected[db] def getUser(login: String): DB[Option[User]] = DB[Option[User]] { connection =>
    safely {
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

  protected[db] def createUser(login: String, password: String): DB[Option[User]] =
    for {
      _ <- insertUser(login, password)
      u <- getUser(login)
    } yield u
}
