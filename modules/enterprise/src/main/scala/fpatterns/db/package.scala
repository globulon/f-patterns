package fpatterns

import fpatterns.validation._
import java.sql.Connection
import scala.language.higherKinds

package object db {
  type DBResult[A] = ReaderT[DomainValidation, Connection, A]

  object DBResult {
    def apply[A](run: Connection => DomainValidation[A]): DBResult[A] = ReaderT[DomainValidation, Connection, A](run)
  }

  type DBAction[A, B] = Reader[Connection, A => DomainValidation[B]]

  object DBAction {
    def apply[A, B](run: Connection => A => DomainValidation[B]): DBAction[A, B] = Reader(run)
  }
}