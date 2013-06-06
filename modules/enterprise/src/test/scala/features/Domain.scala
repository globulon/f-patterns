package features

trait Domain {
  protected case class User(id: Int, login: String, password: String)

  protected case class Address(id: Int, street: String, number: Int)
}
