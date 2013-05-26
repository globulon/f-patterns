package fpatterns

sealed trait Validation[+E, +A]

case class Failure[E, A](tail: Seq[E]) extends Validation[E, A]

object Failure {
  def apply[E, A](e: E): Validation[E, A] = Failure(Seq(e))
}

case class Success[E, A](a: A) extends Validation[E, A]

object Validation {
  def map[E, A, B](va: Validation[E, A])(f: A => B): Validation[E, B] = va match {
    case Success(v) => Success(f(v))
    case Failure(t) => Failure(t)
  }

  def flatMap[E, EE >: E, A, B](va: Validation[E, A])(f: A => Validation[EE, B]): Validation[EE, B] = va match {
    case Success(v) => f(v)
    case Failure(t) => Failure(t)
  }

  def apply[E, A, B](vf: Validation[E, A => B])(va: Validation[E, A]): Validation[E, B] = (va, vf) match {
    case (Success(a), Success(f))   => Success(f(a))
    case (Success(_), Failure(h))   => Failure(h)
    case (Failure(s), Success(_))   => Failure(s)
    case (Failure(sa), Failure(sb)) => Failure(sa ++ sb)
  }
}

trait SpecializedValidation {
  self: Monads =>
  type Error

  type ContextValidation[A] = Validation[Error, A]

  //  implicit def monad: Applicative[ContextValidation] = validationMonad[Error]

  protected def success[A](a: A): ContextValidation[A] = Success(a)

  protected def failure[A](e: Error): ContextValidation[A] = Failure(e)
}

