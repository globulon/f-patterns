package fpatterns

trait Identity[A] {
  def get: A
}

object Identity {
  def apply[A](a: => A): Identity[A] = new Identity[A] {
    def get = a
  }

  def map[A, B](id: Identity[A])(f: A => B): Identity[B] = Identity(f(id.get))

  def flatMap[A, B](id: Identity[A])(f: A => Identity[B]): Identity[B] = f(id.get)

  def map2[A, B, C](ida: Identity[A], idb: Identity[B])(f: (A, B) => C): Identity[C] = Identity(f(ida.get, idb.get))

  def apply[A, B](f: Identity[A => B])(id: Identity[A]): Identity[B] = Identity(f.get(id.get))
}
