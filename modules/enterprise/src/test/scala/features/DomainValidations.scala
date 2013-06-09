package features

import fpatterns.{ Success, Failure }
import fpatterns.validation._

trait DomainValidations {
  self: Domain =>
  type Validate[A] = A => DomainValidation[A]
  type ValidateUser = Validate[User]
  type ValidateAddress = Validate[Address]

  protected def existLogin: ValidateUser = {
    case user if user.login.nonEmpty => Success(user)
    case _                           => Failure("missing login")
  }

  protected def existPassword: ValidateUser = {
    case user if user.password.nonEmpty => Success(user)
    case _                              => Failure("missing password")
  }

  private val passwordRegex = """^.*(?=.{6,})(?=.*[a-zA-Z])(?=.*\d)[a-zA-Z0-9!@#$%]+$""".r

  protected def checkPassword: ValidateUser = user =>
    passwordRegex.findFirstMatchIn(user.password) match {
      case Some(_) => Success(user)
      case _       => Failure("password must contain at least character with 1 digit ")
    }

  protected def validateUser: ValidateUser =
    user => (existLogin(user) |@| existPassword(user) |@| checkPassword(user)) { (_, _, _) => user }

  protected def validateAddressStreet: ValidateAddress = {
    case address if address.street.nonEmpty => Success(address)
    case _                                  => DomainError("Address street must not be empty")
  }

  protected def validateAddressStreetNumber: ValidateAddress = {
    case address if address.number > 0 => Success(address)
    case _                             => DomainError("Address street number must be greater than 0")
  }

  protected def validateAddress: ValidateAddress = address =>
    (validateAddressStreet(address) |@| validateAddressStreetNumber(address)) { (_, _) => address }
}
