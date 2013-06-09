package fpatterns

trait RunnableST[A] {
  def apply[S]: ST[S, A]
}

trait ST[S, A] {
  self =>
  protected def run(s: S): (A, S)

  def map[B](f: A => B): ST[S, B] = ST.map(this)(f)

  def flatMap[B](f: A => ST[S, B]): ST[S, B] = ST.flatMap(this)(f)
}

object ST {
  def apply[S, A](a: => A) = new ST[S, A] {
    lazy val cached = a
    def run(s: S) = (a, s)
  }

  def map[S, A, B](sta: ST[S, A])(f: A => B): ST[S, B] = new ST[S, B] {
    def run(s: S) = {
      val (a, newS) = sta.run(s)
      (f(a), newS)
    }
  }

  def flatMap[S, A, B](sta: ST[S, A])(f: A => ST[S, B]): ST[S, B] = new ST[S, B] {
    def run(s: S) = {
      val (a, newS) = sta.run(s)
      f(a).run(newS)
    }
  }

  def run[A](r: RunnableST[A]): A = r.apply[Unit].run(())._1
}

sealed trait STRef[S, A] {
  self =>
  protected var cell: A

  def read: ST[S, A] = ST(cell)

  def write(a: => A): ST[S, Unit] = new ST[S, Unit] {
    protected def run(s: S) = {
      self.cell = a
      ((), s)
    }
  }
}

object STRef {
  def apply[S, A](a: A): ST[S, STRef[S, A]] = ST[S, STRef[S, A]](new STRef[S, A] {
    protected var cell = a
  })
}
