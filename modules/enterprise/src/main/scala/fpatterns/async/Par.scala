package fpatterns.async

import scala.concurrent.{ Future, ExecutionContext }

trait Par[+A] {
  def run: ExecutionContext => Future[A]

  def map[B](f: A => B): Par[B] = Par.map(this)(f)

  def flatMap[B](f: A => Par[B]): Par[B] = Par.flatMap(this)(f)
}

object Par {
  def apply[A](f: ExecutionContext => Future[A]): Par[A] = new Par[A] { override def run = f }

  def map[A, B](fa: Par[A])(f: A => B): Par[B] = Par[B] { implicit ctx => fa.run(ctx) map f }

  def flatMap[A, B](fa: Par[A])(f: A => Par[B]): Par[B] = Par[B] {
    implicit ctx =>
      for {
        a <- fa.run(ctx)
        b <- f(a).run(ctx)
      } yield b
  }
}

