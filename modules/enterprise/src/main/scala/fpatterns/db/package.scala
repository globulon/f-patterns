package fpatterns

import java.sql.Connection

package object db extends ValidationContext with Monads {
  type Error = String

  type DB[A] = ReaderT[Validation, Connection, A]

  object DB {
    def apply[A](run: Connection => Validation[A]): DB[A] = ReaderT[Validation, Connection, A](run)
  }
}
