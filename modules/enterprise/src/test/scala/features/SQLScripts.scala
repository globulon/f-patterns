package features

trait SQLScripts {
  protected val createUserTable =
    """CREATE TABLE USER(
       |ID INT PRIMARY KEY AUTO_INCREMENT,
       |LOGIN VARCHAR(64) NOT NULL UNIQUE,
       |PASSWORD TEXT NOT NULL)""".stripMargin

  protected val dropUserTable = """DROP TABLE USER"""

  protected val insertUserRecord =
    """INSERT INTO USER (LOGIN, PASSWORD) VALUES(?, ?)"""

  protected val selectUserRecord = """SELECT LOGIN, PASSWORD FROM USER WHERE LOGIN = ?"""
}
