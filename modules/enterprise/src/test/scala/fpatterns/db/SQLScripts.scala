package fpatterns.db

protected[db] trait SQLScripts {
  protected[db] val createUserTable =
    """CREATE TABLE USER(
       |ID INT PRIMARY KEY AUTO_INCREMENT,
       |LOGIN VARCHAR(64) NOT NULL UNIQUE,
       |PASSWORD TEXT NOT NULL)""".stripMargin

  protected[db] val dropUserTable = """DROP TABLE USER"""

  protected[db] val insertUserRecord =
    """INSERT INTO USER (LOGIN, PASSWORD) VALUES(?, ?)"""

  protected[db] val selectUserRecord = """SELECT LOGIN, PASSWORD FROM USER WHERE LOGIN = ?"""
}
