package fpatterns

trait SemiGroup[A] {
  def op: (A, A) => A
}

trait Monoid[A] extends SemiGroup[A] {
  def zero: A
}

trait Monoids {
  type Op[T] = (T, T) => T

  implicit def listMonoid[A]: Monoid[List[A]] = new Monoid[List[A]] {
    override val op: Op[List[A]] = _ ++ _

    def zero = List.empty[A]
  }

  implicit def endoMonoid[A]: Monoid[Endo[A]] = new Monoid[Endo[A]] {
    override val op: Op[Endo[A]] = _.run compose _.run

    override val zero: Endo[A] = Endo(identity[A])
  }
}
