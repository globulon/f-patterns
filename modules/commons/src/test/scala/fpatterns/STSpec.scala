package fpatterns

import org.scalatest.matchers.MustMatchers
import org.scalatest.WordSpec

final class STSpec extends MustMatchers with WordSpec {
  "local state modification" must {
    "compose monadically" in {
      ST.run(runnable) must be ((2,3))
    }
  }

  def runnable: RunnableST[(Int, Int)] = new RunnableST[(Int, Int)] {
    def apply[S] = for {
      r1 <- STRef[S, Int](1)
      r2 <- STRef[S, Int](2)
      x <- r1.read
      y <- r2.read
      _ <- r1.write(x + 1)
      _ <- r2.write(y + 1)
      a <- r1.read
      b <- r2.read
    } yield (a, b)
  }
}