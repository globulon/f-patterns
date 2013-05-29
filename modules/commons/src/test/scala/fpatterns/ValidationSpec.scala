package fpatterns

import org.scalatest.WordSpec
import org.scalatest.matchers.MustMatchers

protected[fpatterns] trait TestValidation extends ValidationContext {
  self: Monads =>
  final type Error = String

  final type DomainValidation[A] = self.Validation[A]
}

final class ValidationSpec extends MustMatchers with WordSpec with ApplicativeBuilders with Domain with TestValidation with Monads {

  "Application on valid objects" must {
    "allow for object construction with 2 parameters" in {
      (success("login") |@| success("password")) { User(_, _) } must be(Success(User("login", "password")))
    }

    "allow for object construction with 3 parameters" in {
      (success("a") |@| success("b") |@| success("c")) { (_, _, _) } must be(Success(("a", "b", "c")))
    }

    "allow for object construction with 4 parameters" in {
      (success("a") |@| success("b") |@| success("c") |@| success("d")) { (_, _, _, _) } must be(Success(("a", "b", "c", "d")))
    }

    "allow for object construction with 5 parameters" in {
      (success("a") |@| success("b") |@| success("c") |@| success("d") |@| success("e")) {
        (_, _, _, _, _)
      } must be(Success(("a", "b", "c", "d", "e")))
    }
  }

  "Application on failures" must {
    "sum 2 failures" in {
      (failure[User]("one") |@| failure[User]("two")) { (_, _) } must be(Failure(List("two", "one")))
    }

    "sum 3 failures" in {
      (failure[User]("one") |@| failure[User]("two") |@| failure[User]("three")) { (_, _, _) } must be(Failure(List("three", "two", "one")))
    }

  }
}
