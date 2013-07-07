package fpatterns

import scala.annotation.tailrec

sealed trait Trampoline[+A] {
  def map[B](f: A => B): Trampoline[B] = Trampoline.map(this)(f)

  def flatMap[B](f: A => Trampoline[B]): Trampoline[B] = Trampoline.flatMap(this)(f)
}

case class Done[+A](a: A) extends Trampoline[A]{
  def get: A = a
}


case class More[+A](force: () => Trampoline[A]) extends Trampoline[A]

case class Bind[A,+B](force: () => Trampoline[A], f: A => Trampoline[B]) extends Trampoline[B]

object Trampoline {
  def unit[A](a: => A) = Done(a)

  def map[A, B](ma: Trampoline[A])(f: A => B): Trampoline[B] = ma match {
    case Done(a) => Done(f(a))
    case More(force) => Bind(force, (a:A) => unit(f(a)))
    case Bind(force, g) => More(() => Bind(force , g andThen (map(_)(f))))
  }

  def flatMap[A, B](ma: Trampoline[A])(f: A => Trampoline[B]): Trampoline[B] = ma match {
    case Done(a) => f(a)
    case More(force) => Bind(force, f)
    case Bind(force,g) => More(() => Bind(force, g andThen (flatMap(_)(f))))
  }

}

trait Trampolines {
  @tailrec
  final protected def run[A](t: Trampoline[A]): A = t match {
    case Done(a) => a
    case More(force) => run(force())
    case Bind(force, f) =>  run(Trampoline.flatMap(force())(f))
  }
}






