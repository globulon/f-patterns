package fpatterns

trait IO[+A] {
  def run: A

  def map[B](f: A => B): IO[B] = IO.map(this)(f)

  def flatMap[B](f: A => IO[B]): IO[B] = IO.flatMap(this)(f)
}

object IO {
  def apply[A](a: => A) = new IO[A] {
    def run = a
  }

  def map[A, B](ioa: IO[A])(f: A => B): IO[B] = IO(f(ioa.run))

  def flatMap[A, B](ioa: IO[A])(f: A => IO[B]): IO[B] = new IO[B] {
    override def run = f(ioa.run).run
  }

  def unit[A](a: => A) = IO(a)

  def map2[A, B, C](ioa: IO[A], iob: IO[B])(f: (A, B) => C): IO[C] = IO(f(ioa.run, iob.run))

  def applyM[A, B](iof: IO[(A) => B])(ioa: IO[A]): IO[B] = IO(iof.run(ioa.run))
}

trait IOs {
  type Convert[A] = Function[String, A]

  protected def write[A](a: A): IO[Unit] = IO(println(a))

  protected def readString: IO[String] = IO(readLine())

  protected def readInt: IO[Int] = readString map (_.toInt)

  protected def readLong: IO[Long] = readString map (_.toLong)

  protected def readDouble: IO[Double] = readString map (_.toDouble)

  protected def readBoolean: IO[Boolean] = readString map (_.toBoolean)

  protected def read[A: Convert] = readString map implicitly[Function[String, A]]

  protected def get[A](a: => A) = IO(a)

  protected def echo = readString flatMap write


  implicit def ioMonad = new Monad[IO] {
    override def unit[A](a: => A): IO[A] = IO.unit(a)

    override def flatMap[A, B](ma: IO[A])(f: (A) => IO[B]): IO[B] = ma flatMap f

    override def map2[A, B, C](fa: IO[A], fb: IO[B])(f: (A, B) => C): IO[C] = IO.map2(fa, fb)(f)

    override def apply[A, B](fab: IO[(A) => B])(fa: IO[A]): IO[B] = IO.applyM(fab)(fa)
  }
}
