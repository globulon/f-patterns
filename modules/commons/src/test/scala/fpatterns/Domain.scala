package fpatterns

trait Domain {
  case class User(login: String, password: String)
}
