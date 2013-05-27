import scala.language.reflectiveCalls

package object fpatterns extends Kleislis with Monads with ApplicativeBuilders {
  protected type Closeable = {
    def close()
  }

  def closing[C <: Closeable, R](c: C)(f: C => R): R =
    try { f(c) } finally { c.close() }

}
