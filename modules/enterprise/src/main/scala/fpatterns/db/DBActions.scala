package fpatterns.db

import fpatterns._
import fpatterns.validation._
import java.sql.Connection

trait DBActions {
  private val M = readerMonad[Connection]

  private val MT = readerTMonad[DomainValidation, Connection]

  protected def action[A, B](f: A => DomainValidation[B]): DBAction[A, B] = M.unit(f)

  protected def result[A](a: => A): DBResult[A] = MT.unit(a)

  def applyRun[A, B](dba: DBAction[A, B])(a: A): DBResult[B] =
    ReaderT.unitT(M.map2(M.unit(a), dba) { (a, k) => a >=: k })

  def compose[A, B, C](dba: DBAction[A, B], dbb: DBAction[B, C]): DBAction[A, C] =
    M.map2(dba, dbb) { (fa, fb) => (fa >=> fb).run }

  implicit class RichDBAction[A, B](val dba: DBAction[A, B]) {
    def >=>[C](dbb: DBAction[B, C]): DBAction[A, C] = compose[A, B, C](dba, dbb)

    def >=:(a: A): DBResult[B] = applyRun[A, B](dba)(a)
  }
}
