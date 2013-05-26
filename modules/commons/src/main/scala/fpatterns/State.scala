package fpatterns

trait State[S, A] extends ((S) => (S, A)) {
  self =>

  def flatMap[B](continuation: A => State[S, B]) =
    State.flatMap(self)(continuation)

  def map[B](f: A => B) = State.map(self)(f)
}

object State {
  def flatMap[S, A, B](monadicValue: State[S, A])(continuation: A => State[S, B]) =
    new State[S, B] {
      def apply(state: S) = {
        val tuple = monadicValue(state)
        val bindingResult = tuple._2
        val newState = tuple._1
        continuation(bindingResult)(newState)
      }
    }

  def map[S, A, B](monadicValue: State[S, A])(f: A => B) =
    new State[S, B] {
      def apply(state: S) = {
        val tuple = monadicValue(state)
        val bindingResult = tuple._2
        val newState = tuple._1
        (newState, f(bindingResult))
      }
    }

  def apply[S, A](f: (S) => (S, A)) = new State[S, A] {
    def apply(s: S): (S, A) = f(s)
  }
}