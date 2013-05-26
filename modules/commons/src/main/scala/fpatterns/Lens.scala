package fpatterns

trait Lens[A, B] extends ((A) => B) {

  def set: (A, B) => A

  def get: (A) => B

  def apply(a: A): B = get(a)

  implicit def toState = Lens.toState(this)

  def assign(b: B): State[A, Unit] = Lens assign (this, b)

  def mod(a: A, f: B => B): A = Lens.modify(this)(f).apply(a)

  def compose[C](l: Lens[C, A]): Lens[C, B] = Lens.compose(this)(l)

  def map[C](f: B => C) = Lens.map(this)(f)

  def flatMap[C](f: B => State[A, C]) = Lens.flatMap(this)(f)
}

object Lens {
  def apply[A, B](g: (A) => B, s: (A, B) => A) = new Lens[A, B] {
    override val set = s

    override val get = g
  }

  def toState[A, B](l: Lens[A, B]): State[A, B] = State { a => (a, l.get(a)) }

  def modify[A, B](l: Lens[A, B])(f: B => B): (A) => A = (a) => l.set(a, f(l.get(a)))

  def compose[A, B, C](l1: Lens[A, B])(l2: Lens[C, A]): Lens[C, B] = Lens(
    c => l1.get(l2.get(c)),
    (c, b) => modify(l2) { a => l1.set(a, b) }.apply(c))

  def assign[A, B](l: Lens[A, B], b: B): State[A, Unit] = State { a => (l.set(a, b), ()) }

  def map[A, B, C](l: Lens[A, B])(f: B => C): State[A, C] = State { a => (a, f(l.get(a))) }

  def flatMap[A, B, C](l: Lens[A, B])(f: B => State[A, C]): State[A, C] = State { a => f(l.get(a))(a) }
}