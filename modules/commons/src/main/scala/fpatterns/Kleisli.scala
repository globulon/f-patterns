package fpatterns

import scala.language.{ higherKinds, implicitConversions }

trait Kleisli[M[_], A, B] {
  def run: A => M[B]

  def >=>[C](other: Kleisli[M, B, C])(implicit m: Monad[M]): Kleisli[M, A, C] = Kleisli.compose(this, other)

  def >=>[C](other: B => M[C])(implicit m: Monad[M]): Kleisli[M, A, C] = this >=> Kleisli[M, B, C](other)

  def >=:(a: A): M[B] = Kleisli.run(this)(a)
}

object Kleisli {
  def apply[M[_]: Monad, A, B](f: A => M[B]): Kleisli[M, A, B] = new Kleisli[M, A, B] {
    override val run = f
  }

  def compose[M[_]: Monad, A, B, C](ka: Kleisli[M, A, B], kb: Kleisli[M, B, C]): Kleisli[M, A, C] =
    Kleisli[M, A, C] { a => implicitly[Monad[M]].flatMap(ka.run(a))(kb.run) }

  def run[M[_], A, B](ka: Kleisli[M, A, B])(a: A): M[B] = ka.run(a)
}

trait Kleislis {
  implicit def kleisi[M[_]: Monad, A, B](f: A => M[B]): Kleisli[M, A, B] = Kleisli(f)
}
