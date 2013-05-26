package fpatterns

import scala.language.implicitConversions

trait Endo[A] {
  def run: A => A

  def |+|(other: Endo[A])(implicit m: Monoid[Endo[A]]): Endo[A] = m op (this, other)
}

object Endo {
  def apply[A](f: A => A): Endo[A] = new Endo[A] { override val run = f }

  implicit def toEndo[A](f: A => A): Endo[A] = Endo(f)
}
