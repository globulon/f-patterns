package fpatterns

import scala.language.higherKinds

trait ReaderT[M[_], Ctx, A] {
  def run: Ctx => M[A]

  def map[B](f: A => B)(implicit m: Monad[M]): ReaderT[M, Ctx, B] = ReaderT.map(this)(f)

  def flatMap[B](f: A => ReaderT[M, Ctx, B])(implicit m: Monad[M]): ReaderT[M, Ctx, B] = ReaderT.flatMap(this)(f)
}

object ReaderT {
  def apply[M[_]: Monad, Ctx, A](f: Ctx => M[A]): ReaderT[M, Ctx, A] =
    new ReaderT[M, Ctx, A] { override val run = f }

  def map[M[_]: Monad, Ctx, A, B](rta: ReaderT[M, Ctx, A])(f: A => B): ReaderT[M, Ctx, B] =
    ReaderT[M, Ctx, B] { ctx => implicitly[Monad[M]].map(rta.run(ctx))(f) }

  def flatMap[M[_]: Monad, Ctx, A, B](rta: ReaderT[M, Ctx, A])(f: A => ReaderT[M, Ctx, B]): ReaderT[M, Ctx, B] =
    ReaderT[M, Ctx, B] { ctx => implicitly[Monad[M]].flatMap(rta.run(ctx))(f(_).run(ctx)) }

  def map2[M[_]: Monad, Ctx, A, B, C](rta: ReaderT[M, Ctx, A], rtb: ReaderT[M, Ctx, B])(f: (A, B) => C): ReaderT[M, Ctx, C] =
    ReaderT[M, Ctx, C] { ctx => implicitly[Monad[M]].map2(rta.run(ctx), rtb.run(ctx))(f) }

  def apply[M[_]: Monad, Ctx, A, B](rtf: ReaderT[M, Ctx, A => B])(rta: ReaderT[M, Ctx, A]): ReaderT[M, Ctx, B] =
    ReaderT[M, Ctx, B] { ctx => implicitly[Monad[M]].apply(rtf.run(ctx))(rta.run(ctx)) }

  def unit[M[_]: Monad, Ctx, A](a: A) = ReaderT[M, Ctx, A] { _ => implicitly[Monad[M]].unit(a) }
}