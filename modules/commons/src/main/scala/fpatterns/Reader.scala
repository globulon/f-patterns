package fpatterns

trait Reader[Ctx, A] {
  def run: Ctx => A

  def map[B](f: A => B): Reader[Ctx, B] = Reader.map(this)(f)

  def flatMap[B](f: A => Reader[Ctx, B]): Reader[Ctx, B] = Reader.flatMap(this)(f)

  def compose[B](other: Reader[A, B]): Reader[Ctx, B] = Reader.compose(this)(other)
}

object Reader {
  def apply[Ctx, A](get: Ctx => A): Reader[Ctx, A] = new Reader[Ctx, A] {
    override val run = get
  }

  def map[Ctx, A, B](ra: Reader[Ctx, A])(f: A => B): Reader[Ctx, B] = Reader(ra.run andThen f)

  def flatMap[Ctx, A, B](ra: Reader[Ctx, A])(f: A => Reader[Ctx, B]): Reader[Ctx, B] =
    Reader { r => f(ra.run(r)).run(r) }

  def ask[Ctx] = Reader[Ctx, Ctx](identity)

  def compose[Ctx, A, B](rc: Reader[Ctx, A])(ra: Reader[A, B]): Reader[Ctx, B] =
    Reader(rc.run andThen ra.run)

  def map2[Ctx, A, B, C](ra: Reader[Ctx, A], rb: Reader[Ctx, B])(f: (A, B) => C): Reader[Ctx, C] =
    Reader { ctx => f(ra.run(ctx), rb.run(ctx)) }

  def apply[Ctx, A, B](rf: Reader[Ctx, A => B])(ra: Reader[Ctx, A]): Reader[Ctx, B] =
    Reader { ctx => rf.run(ctx).apply(ra.run(ctx)) }
}