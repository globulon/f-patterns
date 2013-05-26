import scala.language.reflectiveCalls

package object fpatterns {
  protected type Closeable = {
    def close()
  }

  def closing[C <: Closeable, R](c: C)(f: C => R): R =
    try { f(c) } finally { c.close() }
}
