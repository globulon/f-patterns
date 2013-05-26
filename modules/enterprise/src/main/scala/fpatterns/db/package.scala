package fpatterns

import java.sql.Connection

package object db {
  type DB[A] = Reader[Connection, A]

  object DB {
    def apply[A](get: Connection => A): DB[A] = new Reader[Connection, A] {
      override val run = get
    }
  }
}
