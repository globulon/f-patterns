package fpatterns

import scala.language.higherKinds

trait StateT[S, M[_], A] extends ((S) => M[(S, A)]) {
  def map[B](f: A => B)(implicit m: Monad[M]): StateT[S, M, B] = StateT.map(this)(f)

  def flatMap[B](f: A => StateT[S, M, B])(implicit m: Monad[M]): StateT[S, M, B] = StateT.flatMap(this)(f)
}

object StateT {
  private def M[M[_]: Monad] = implicitly[Monad[M]]

  def map[S, M[_]: Monad, A, B](sta: StateT[S, M, A])(f: A => B) = new StateT[S, M, B] {

    def apply(s: S): M[(S, B)] = M[M].map(sta(s)) { case (newS, a) => (newS, f(a)) }
  }

  def flatMap[S, M[_]: Monad, A, B](sta: StateT[S, M, A])(f: A => StateT[S, M, B]) = new StateT[S, M, B] {

    def apply(s: S): M[(S, B)] = M[M].flatMap(sta(s)) { case (newS, a) => f(a)(newS) }
  }

  def apply[S, M[_], A](f: S => M[(S, A)]): StateT[S, M, A] = new StateT[S, M, A] {
    def apply(s: S): M[(S, A)] = f(s)
  }
}

trait StateTs {
  private def M[M[_]: Monad] = implicitly[Monad[M]]

  protected def init[S, M[_]: Monad]: StateT[S, M, S] = StateT[S, M, S] { s => M[M].unit((s, s)) }

  protected def gets[S, M[_]: Monad, A](f: S => A): StateT[S, M, A] = init[S, M] map f

  protected def getM[S, M[_]: Monad, A](f: S => M[A]): StateT[S, M, A] = StateT[S, M, A] { s => M[M].map(f(s))((s, _)) }

  protected def setT[S, M[_]: Monad](s: S): StateT[S, M, Unit] = StateT[S, M, Unit] { _ => M[M].unit((s, ())) }
}
