package fpatterns.test.helpers

import fpatterns.validation._
import fpatterns.{ Failure, Success }
import org.scalatest.Suite

trait Assertions {
  self: Suite =>
  protected def checkSuccess[A](v: DomainValidation[A])(f: A => Unit) =
    v match {
      case Success(a)  => f(a)
      case Failure(ms) => fail(s"Success expected: [${(ms)}]")
    }

  protected def checkFailure[A](v: DomainValidation[A])(f: Seq[String] => Unit) =
    v match {
      case Failure(ms) => f(ms)
      case Success(a)  => fail(s"Failure expected: [${(a)}]")
    }
}
