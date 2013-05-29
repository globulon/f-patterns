package fpatterns

import scala.util.control.Exception._

package object validation {
  type DomainValidation[A] = Validation[String, A]

  type DomainError[A] = Seq[String]

  object DomainError {
    def apply[A](m: String): Validation[String, A] = Failure(Seq(m))

    def apply[A](ms: Seq[String]): Validation[String, A] = Failure(ms)
  }

  protected val defaultExceptionHandling: Throwable => String = "%s" format (_)

  def safely[T](process: => T, onError: => (Throwable) => String = defaultExceptionHandling): DomainValidation[T] =
    catching(classOf[Throwable]).either(process) match {
      case Right(result)   => Success(result)
      case Left(exception) => Failure(onError(exception))
    }

}
