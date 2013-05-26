package fpatterns

trait Writer[W, A] {
  def get: (W, A)

  def map[B](f: A => B): Writer[W, B] = Writer.map(this)(f)

  def flatMap[B](f: A => Writer[W, B])(implicit m: Monoid[W]): Writer[W, B] = Writer.flatMap(this)(f)

}

object Writer {
  def apply[W, A](value: (W, A)) = new Writer[W, A] {
    override val get = value
  }

  def unapply[W, A](wr: Writer[W, A]): Option[(W, A)] = Some(wr.get)

  def map[W, A, B](wa: Writer[W, A])(f: A => B): Writer[W, B] = new Writer[W, B] {
    override val get = (wa.get._1, f(wa.get._2))
  }

  def flatMap[W: Monoid, A, B](wa: Writer[W, A])(f: A => Writer[W, B]): Writer[W, B] = new Writer[W, B] {
    override val get = f(wa.get._2) match {
      case Writer((w, b)) => (implicitly[Monoid[W]].op(wa.get._1, w), b)
    }
  }
}

