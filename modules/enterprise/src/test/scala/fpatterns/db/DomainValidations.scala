package fpatterns.db

import domain.Domain
import fpatterns._
import fpatterns.validation.DomainValidation

protected[db] trait DomainValidations {
  self: Domain =>
  type Validate[A] = A => DomainValidation[A]
  type ValidateUser = Validate[User]

  def existLogin: ValidateUser = {
    case user if user.login.nonEmpty => Success(user)
    case _                           => Failure("missing login")
  }

  def existPassword: ValidateUser = {
    case user if user.password.nonEmpty => Success(user)
    case _                              => Failure("missing password")
  }

  private val passwordRegex = """^.*(?=.{6,})(?=.*[a-zA-Z])(?=.*\d)[a-zA-Z0-9!@#$%]+$""".r

  def checkPassword: ValidateUser = user =>
    passwordRegex.findFirstMatchIn(user.password) match {
      case Some(_) => Success(user)
      case _       => Failure("password must contain at least character with 1 digit ")
    }

  def validateUser: ValidateUser =
    user => (existLogin(user) |@| existPassword(user) |@| checkPassword(user)) { (_, _, _) => user }
}
