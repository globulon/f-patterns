package fpatterns

import scala.language.higherKinds

trait State[S, A] extends ((S) => (S, A)) {
  self =>

  def flatMap[B](sb: A => State[S, B]) = State.flatMap(self)(sb)

  def map[B](f: A => B) = State.map(self)(f)
}

object State {
  def flatMap[S, A, B](sa: State[S, A])(asb: A => State[S, B]) = new State[S, B] {
    def apply(state: S) = {
      val (newState, result) = sa(state)
      asb(result)(newState)
    }
  }

  def map[S, A, B](sa: State[S, A])(f: A => B) = new State[S, B] {
    def apply(state: S) = {
      val (newState, result) = sa(state)
      (newState, f(result))
    }
  }

  def apply[S, A](f: (S) => (S, A)) = new State[S, A] {
    def apply(s: S): (S, A) = f(s)
  }
}

trait States {
  def set[S](s: S): State[S, Unit] = State { _ => (s, ()) }

  def get[S]: State[S, S] = State { s => (s, s) }
}

trait StateT[S, M[_], A] extends ((S) => M[(S, A)]) {
  def map[B](f: A => B)(implicit m: Monad[M]): StateT[S, M, B] = StateT.map(this)(f)

  def flatMap[B](f: A => StateT[S, M, B])(implicit m: Monad[M]): StateT[S, M, B] = StateT.flatMap(this)(f)
}

object StateT {
  def map[S, M[_]: Monad, A, B](sta: StateT[S, M, A])(f: A => B) = new StateT[S, M, B] {
    private def M = implicitly[Monad[M]]

    def apply(s: S): M[(S, B)] = M.map(sta(s)) { case (newS, a) => (newS, f(a)) }
  }

  def flatMap[S, M[_]: Monad, A, B](sta: StateT[S, M, A])(f: A => StateT[S, M, B]) = new StateT[S, M, B] {
    private def M = implicitly[Monad[M]]

    def apply(s: S): M[(S, B)] = M.flatMap(sta(s)) { case (newS, a) => f(a)(newS) }
  }

  def apply[S, M[_], A](f: S => M[(S, A)]): StateT[S, M, A] = new StateT[S, M, A] {
    def apply(s: S): M[(S, A)] = f(s)
  }
}