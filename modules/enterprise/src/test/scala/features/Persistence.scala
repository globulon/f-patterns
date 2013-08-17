package features

import fpatterns._
import fpatterns.db._
import fpatterns.validation._

trait Persistence {
  self: SQLScripts with Domain =>
  private def executeStatment(script: String): DBResult[Unit] =
    DBResult[Unit] { connection => safe { closing(connection.createStatement())(_.execute(script)) } }

  protected def createTableUser: DBResult[Unit] = executeStatment(createUserTable)

  protected def dropTableUser: DBResult[Unit] = executeStatment(dropUserTable)

  protected def createTableAddress: DBResult[Unit] = executeStatment(createAddressTable)

  protected def dropTableAddress: DBResult[Unit] = executeStatment(dropAddressTable)

  protected def createUser: DBAction[User, Int] = DBAction[User, Int] { connection =>
    (user) => safe {
      closing(connection.prepareStatement(insertUserRecord)) { statement =>
        statement.setString(1, user.login)
        statement.setString(2, user.password)
        statement.executeUpdate()
      }
    }
  }

  protected def createAddress(user: User): DBAction[Address, Int] = DBAction[Address, Int] { connection =>
    (address) => safe {
      closing(connection.prepareStatement(insertAddressRecord)) { statement =>
        statement.setString(1, address.street)
        statement.setInt(2, address.number)
        statement.setInt(3, user.id)
        statement.executeUpdate()
      }
    }
  }

  protected def readUser: DBAction[String, Option[User]] = DBAction[String, Option[User]] { connection =>
    (login) => safe {
      closing(connection.prepareStatement(selectUserByLogin)) { statement =>
        statement.setString(1, login)
        closing(statement.executeQuery()) { rs =>
          rs.next() match {
            case true => Some(User(rs.getInt("USER.ID"), rs.getString("USER.LOGIN"), rs.getString("USER.PASSWORD")))
            case _    => None
          }
        }
      }
    }
  }

  protected def readAddress: DBAction[User, Option[Address]] = DBAction[User, Option[Address]] { connection =>
    (user) => safe {
      closing(connection.prepareStatement(selectAddressByUser)) { statement =>
        statement.setInt(1, user.id)
        closing(statement.executeQuery()) { rs =>
          rs.next() match {
            case true => Some(Address(rs.getInt("ADDRESS.ID"), rs.getString("ADDRESS.STREET"), rs.getInt("ADDRESS.NUMBER")))
            case _    => None
          }
        }
      }
    }
  }
}
