package fpatterns.async

import scala.concurrent.{ ExecutionContext, Future }

trait ProvideContext {
  def apply[A](p: Par[A]): Future[A]
}

object ProvideContext {
  def apply(ctx: ExecutionContext): ProvideContext = new ProvideContext {
    def apply[A](p: Par[A]) = p.run(ctx)
  }
}
