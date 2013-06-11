package fpatterns.async

import fpatterns.Monad
import scala.concurrent.duration._
import scala.concurrent.{ Await, Future }

trait Async {
  protected implicit val ParM: Monad[Par] = new Monad[Par] {
    def flatMap[A, B](ma: Par[A])(f: (A) => Par[B]) = ma flatMap f

    def unit[A](a: => A): Par[A] = Par[A] { implicit ctx => Future(a) }

    override def map[A, B](fa: Par[A])(f: (A) => B) = fa map f

    override def apply[A, B](fab: Par[(A) => B])(fa: Par[A]) = Par[B] { implicit ctx =>
      for {
        f <- fab.run(ctx)
        a <- fa.run(ctx)
      } yield f(a)
    }
  }

  def fork[A](p: Par[A]): Par[A] = Par[A] { implicit ctx => Future(p.run(ctx)).flatMap(identity) }

  def makeAsync[A, B](f: A => B): A => Par[B] = a => ParM.unit(f(a))
}

trait AsyncContext extends Async {
  def provideContext: ProvideContext

  def defaultDuration: Duration

  def run[A](par: Par[A]): A = Await.result(provideContext(par), defaultDuration)
}
