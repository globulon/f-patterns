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
  protected def set[S](s: S): State[S, Unit] = State { _ => (s, ()) }

  protected def init[S]: State[S, S] = State { s => (s, s) }

  protected def gets[S, A](f: S => A): State[S, A] = init[S] map f
}
