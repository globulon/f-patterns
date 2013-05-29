package features

trait Domain {
  case class User(login: String, password: String)
}
