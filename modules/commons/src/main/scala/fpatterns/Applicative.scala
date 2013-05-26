package fpatterns

import scala.language.higherKinds

trait Applicative[F[_]] extends Functor[F] {
  def map2[A, B, C](fa: F[A], fb: F[B])(f: (A, B) => C): F[C] = apply(apply(unit(f.curried))(fa))(fb)

  def map3[A, B, C, D](fa: F[A], fb: F[B], fc: F[C])(f: (A, B, C) => D): F[D] =
    apply(apply(apply(unit(f.curried))(fa))(fb))(fc)

  def map4[A, B, C, D, E](fa: F[A], fb: F[B], fc: F[C], fd: F[D])(f: (A, B, C, D) => E): F[E] =
    apply(apply(apply(apply(unit(f.curried))(fa))(fb))(fc))(fd)

  def map5[A, B, C, D, E, G](fa: F[A], fb: F[B], fc: F[C], fd: F[D], fe: F[E])(f: (A, B, C, D, E) => G): F[G] =
    apply(apply(apply(apply(apply(unit(f.curried))(fa))(fb))(fc))(fd))(fe)

  def map6[A, B, C, D, E, G, H](fa: F[A], fb: F[B], fc: F[C], fd: F[D], fe: F[E], fg: F[G])(f: (A, B, C, D, E, G) => H): F[H] =
    apply(apply(apply(apply(apply(apply(unit(f.curried))(fa))(fb))(fc))(fd))(fe))(fg)

  def apply[A, B](fab: F[A => B])(fa: F[A]): F[B] = map2(fab, fa)(_.apply(_))

  def unit[A](a: => A): F[A]

  def map[A, B](fa: F[A])(f: A => B): F[B] = apply(unit(f))(fa)

  def sequence[A](fas: List[F[A]]): F[List[A]] = traverse(fas)(identity)

  def traverse[A, B](as: List[A])(f: A => F[B]): F[List[B]] =
    as.foldRight(unit(List.empty[B])) { (a, fl) => map2(fl, f(a))(_ :+ _) }

  def replicateM[A](n: Int, fa: F[A]): F[List[A]] = sequence(List.fill(n)(fa))

  def factor[A, B](ma: F[A], mb: F[B]): F[(A, B)] = map2(ma, mb)((_, _))
}

trait ApplicativeBuilders {

  implicit class Builder[F[_]: Applicative, A](val fa: F[A]) {
    def |@|[B](fb: F[B]): Builder2[F, A, B] = Builder2[F, A, B](fa, fb)
  }

  case class Builder2[F[_]: Applicative, A, B](fa: F[A], fb: F[B]) {
    private def F = implicitly[Applicative[F]]

    def apply[C](f: (A, B) => C): F[C] = F.map2(fa, fb)(f)

    def |@|[C](fc: F[C]) = Builder3[F, A, B, C](fa, fb, fc)
  }

  case class Builder3[F[_]: Applicative, A, B, C](fa: F[A], fb: F[B], fc: F[C]) {
    private def F = implicitly[Applicative[F]]

    def apply[D](f: (A, B, C) => D): F[D] = F.map3(fa, fb, fc)(f)

    def |@|[D](fd: F[D]) = Builder4[F, A, B, C, D](fa, fb, fc, fd)
  }

  case class Builder4[F[_]: Applicative, A, B, C, D](fa: F[A], fb: F[B], fc: F[C], fd: F[D]) {
    private def F = implicitly[Applicative[F]]

    def apply[E](f: (A, B, C, D) => E): F[E] = F.map4(fa, fb, fc, fd)(f)

    def |@|[E](fe: F[E]) = Builder5[F, A, B, C, D, E](fa, fb, fc, fd, fe)
  }

  case class Builder5[F[_]: Applicative, A, B, C, D, E](fa: F[A], fb: F[B], fc: F[C], fd: F[D], fe: F[E]) {
    private def F = implicitly[Applicative[F]]

    def apply[G](f: (A, B, C, D, E) => G): F[G] = F.map5(fa, fb, fc, fd, fe)(f)

    def |@|[G](fg: F[G]) = Builder6[F, A, B, C, D, E, G](fa, fb, fc, fd, fe, fg)
  }

  case class Builder6[F[_]: Applicative, A, B, C, D, E, G](fa: F[A], fb: F[B], fc: F[C], fd: F[D], fe: F[E], fg: F[G]) {
    private def F = implicitly[Applicative[F]]

    def apply[H](f: (A, B, C, D, E, G) => H): F[H] = F.map6(fa, fb, fc, fd, fe, fg)(f)
  }
}