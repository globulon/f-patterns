package fpatterns

import scala.language.postfixOps

trait TemperatureConversion {
  protected def fahrenheitToCelsius(f: Double): Double = (f - 32) * 5.0/9.0
}

object IOSample1 extends IOs with TemperatureConversion {

  private def makeConversion = for {
    _ <- write("Give a temperature in fahrenheit")
    t <- readDouble
    r <- write(s"to Celsius degrees: ${fahrenheitToCelsius(t)}")
  } yield r

  def main(args: Array[String]) = makeConversion run
}
