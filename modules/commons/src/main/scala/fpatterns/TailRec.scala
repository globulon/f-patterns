package fpatterns

import scala.annotation.tailrec

trait TailRec {
  sealed trait Bounce[+A]

  case class Done[+A](get: A) extends Bounce[A]

  case class Call[A](f: () => Bounce[A]) extends Bounce[A]

  @tailrec
  final protected def trampoline[A](ba: Bounce[A]): A = ba match {
    case Done(get) => get
    case Call(f)   => trampoline(f())
  }
}
