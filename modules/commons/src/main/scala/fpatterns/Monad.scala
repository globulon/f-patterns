package fpatterns

import scala.language.higherKinds

trait Functor[F[_]] {
  def map[A, B](fa: F[A])(f: A => B): F[B]
}

trait Monad[M[_]] extends Applicative[M] {
  def unit[A](a: => A): M[A]

  def flatMap[A, B](ma: M[A])(f: A => M[B]): M[B]

  //  override def map[A, B](ma: M[A])(f: A => B): M[B] = flatMap(ma)(a => unit(f(a)))

  //  def map2[A, B, C](ma: M[A], mb: M[B])(f: (A, B) => C): M[C] =
  //    flatMap(ma) { a =>
  //      map(mb) { b => f(a, b) }
  //    }

  def replicate[A](n: Int)(ma: M[A]): M[List[A]] = sequence(List.fill(n)(ma))

  def compose[A, B, C](f: A => M[B])(g: B => M[C]): A => M[C] = a => flatMap(f(a)) { b => g(b) }

  def join[A](mma: M[M[A]]): M[A] = flatMap(mma)(identity)
}

trait Monads {
  implicit def readerMonad[Ctx] = new Monad[({ type lambda[A] = Reader[Ctx, A] })#lambda] {
    def unit[A](a: => A): Reader[Ctx, A] = Reader { _ => a }

    def flatMap[A, B](ma: Reader[Ctx, A])(f: (A) => Reader[Ctx, B]) = Reader.flatMap(ma)(f)

    override def map[A, B](ma: Reader[Ctx, A])(f: A => B) = Reader.map(ma)(f)

    override def apply[A, B](fab: Reader[Ctx, A => B])(fa: Reader[Ctx, A]): Reader[Ctx, B] =
      Reader.apply(fab)(fa)
  }

  def stateMonad[S] = new Monad[({ type lambda[A] = State[S, A] })#lambda] {
    def unit[A](a: => A): State[S, A] = State(s => (s, a))

    def flatMap[A, B](s: State[S, A])(f: (A) => State[S, B]): State[S, B] = State.flatMap(s)(f)

    override def map[A, B](s: State[S, A])(f: (A) => B): State[S, B] = State.map(s)(f)

    override def map2[A, B, C](fa: State[S, A], fb: State[S, B])(f: (A, B) => C) =
      for {
        a <- fa
        b <- fb
      } yield f(a, b)
  }

  implicit def validationMonad[E] = new Monad[({ type lambda[A] = Validation[E, A] })#lambda] {
    override def apply[A, B](fab: Validation[E, (A) => B])(fa: Validation[E, A]) = Validation.apply(fab)(fa)

    def unit[A](a: => A) = Success(a)

    override def map[A, B](va: Validation[E, A])(f: (A) => B) = Validation.map(va)(f)

    def flatMap[A, B](ma: Validation[E, A])(f: (A) => Validation[E, B]): Validation[E, B] = Validation.flatMap(ma)(f)
  }

  def writerMonad[W: Monoid] = new Monad[({ type lambda[A] = Writer[W, A] })#lambda] {
    private def M: Monoid[W] = implicitly[Monoid[W]]

    override def map[A, B](ma: Writer[W, A])(f: (A) => B): Writer[W, B] = Writer.map(ma)(f)

    def flatMap[A, B](ma: Writer[W, A])(f: (A) => Writer[W, B]): Writer[W, B] = Writer.flatMap(ma)(f)

    override def map2[A, B, C](fa: Writer[W, A], fb: Writer[W, B])(f: (A, B) => C) =
      Writer((M.op(fa.get._1, fb.get._1), f(fa.get._2, fb.get._2)))

    def unit[A](a: => A) = Writer((implicitly[Monoid[W]].zero, a))
  }
}