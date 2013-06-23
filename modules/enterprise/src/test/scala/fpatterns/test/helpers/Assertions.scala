package fpatterns.test.helpers

import fpatterns.validation._
import fpatterns.{ Failure, Success }
import org.scalatest.Suite
import fpatterns.async.{ Par, AsyncContext }

trait Assertions {
  self: Suite =>

  protected def checkSuccess[A](v: DomainValidation[A])(f: A => Unit) =
    v match {
      case Success(a)  => f(a)
      case Failure(ms) => fail(s"Success expected: [$ms]")
    }

  protected def checkFailure[A](v: DomainValidation[A])(f: Seq[String] => Unit) =
    v match {
      case Failure(ms) => f(ms)
      case Success(a)  => fail(s"Failure expected: [$a]")
    }
}

trait AsyncAssertions extends Assertions {
  self: Suite with AsyncContext =>

  protected def checkAsyncSuccess[A](v: Par[DomainValidation[A]])(f: A => Unit) =
    safely(run(v)) match {
      case Success(result) => checkSuccess(result)(f)
      case Failure(ms)     => fail(s"Failure running Par: [$ms]")
    }

  protected def checkAsyncFailure[A](v: Par[DomainValidation[A]])(f: Seq[String] => Unit) =
    safely(run(v)) match {
      case Success(result) => checkFailure(result)(f)
      case Failure(ms)     => fail(s"Failure running Par: [$ms]")
    }
}
