import scala.language.reflectiveCalls

import scala.util.control.Exception._

package object fpatterns {
  protected type Closeable = {
    def close()
  }

  def closing[C <: Closeable, R](c: C)(f: C => R): R =
    try { f(c) } finally { c.close() }

  protected val defaultExceptionHandling: Throwable => String = "%s" format (_)

  def safely[T](process: => T, onError: => (Throwable) => String = defaultExceptionHandling): Validation[String, T] =
    catching(classOf[Throwable]).either(process) match {
      case Right(result)   => Success(result)
      case Left(exception) => Failure(onError(exception))
    }
}
