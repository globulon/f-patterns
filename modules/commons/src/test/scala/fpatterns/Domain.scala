package fpatterns

protected trait Domain {
  protected case class User(login: String, password: String)
}
