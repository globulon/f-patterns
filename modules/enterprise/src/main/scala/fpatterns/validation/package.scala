package fpatterns

import scala.util.control.Exception._

package object validation {
  type DomainValidation[A] = Validation[String, A]

  type DomainError[A] = Seq[String]

  object DomainError {
    def apply[A](m: String): DomainValidation[A] = Failure(Seq(m))

    def apply[A](ms: Seq[String]): DomainValidation[A] = Failure(ms)
  }

  protected val defaultExceptionHandling: Throwable => String = "%s" format (_)

  def safe[A](process: => A, onError: => (Throwable) => String = defaultExceptionHandling): DomainValidation[A] =
    catching(classOf[Throwable]).either(process) match {
      case Right(result)   => Success(result)
      case Left(exception) => Failure(onError(exception))
    }

  def safely[A, B](f: A => B, onError: => (Throwable) => String = defaultExceptionHandling): A => DomainValidation[B] =
    a => safe(f(a))

  def whenFail[A, B](v: A => DomainValidation[B])(g: (DomainValidation[B], A) => DomainValidation[B]): A => DomainValidation[B] =
    (a) => v(a) match {
      case s @ Success(_) => s
      case f @ _          => g(f, a)
    }

  def expected[A](error: String): (DomainValidation[Option[A]]) => DomainValidation[A] = {
    case Success(Some(a))  => Success(a)
    case Success(None)     => Failure(error)
    case Failure(messages) => Failure(messages)
  }

  def expect[A, B](error: String)(v: A => DomainValidation[Option[B]]): A => DomainValidation[B] =
    a => expected(error)(v(a))

  //  def expect[A, B](error: String)(v: A => Option[B]): A => DomainValidation[B] =
  //    a => v(a) match  {
  //      case Some(a) => Success(a)
  //      case None  => DomainError(error)
  //    }

}
