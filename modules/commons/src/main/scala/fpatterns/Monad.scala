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

  def foldM[A, B](as: Stream[A])(z: B)(f: (B, A) => M[B]): M[B] =
    as match {
      case h #:: t => flatMap(f(z, h))(z1 => foldM(t)(z1)(f))
      case _       => unit(z)
    }

  def doWhile[A](ma: M[A])(p: A => M[Boolean]): M[Unit] = flatMap(ma) { a =>
    flatMap(p(a)) { if (_) doWhile(ma)(p) else unit(()) }
  }
}

