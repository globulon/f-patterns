package fpatterns.db

import fpatterns._
import fpatterns.validation._
import java.sql.Connection

trait TransactionManager {
  private def rollback: Connection => DomainValidation[Unit] = safely(_.rollback())

  private def run[A](dba: DBResult[A]): StateT[Connection, DomainValidation, A] =
    StateT[Connection, DomainValidation, A] { c => dba.run(c) map ((c, _)) }

  private def setAutocommit(b: Boolean): StateT[Connection, DomainValidation, Unit] =
    StateT[Connection, DomainValidation, Unit] { c => safe(c.setAutoCommit(b)) map { _ => (c, ()) } }

  private def commit: StateT[Connection, DomainValidation, Unit] =
    StateT[Connection, DomainValidation, Unit] { c => safe(c.commit()) map { _ => (c, ()) } }

  private def makeCommit[A](dba: DBResult[A]): StateT[Connection, DomainValidation, A] =
    for {
      _ <- setAutocommit(b = false)
      a <- run(dba)
      _ <- commit
    } yield a

  def makeTx[A](dba: DBResult[A]): DBResult[A] = DBResult[A] {
    whenFail(makeCommit(dba)) { (r, conn) =>
      (r |@| rollback(conn))((r, _) => r)
    } andThen (_.map(_._2))
  }
}
