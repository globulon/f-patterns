package domain

trait Domain {
  case class User(login: String, password: String)
}
