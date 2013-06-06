package features

trait SQLScripts {
  protected val createUserTable =
    """CREATE TABLE USER(
       |ID INT PRIMARY KEY AUTO_INCREMENT,
       |LOGIN VARCHAR(64) NOT NULL UNIQUE,
       |PASSWORD TEXT NOT NULL)""".stripMargin

  protected val createAddressTable =
    """CREATE TABLE ADDRESS (
      |ID INT PRIMARY KEY AUTO_INCREMENT,
      |STREET VARCHAR(64) NOT NULL,
      |NUMBER INT NOT NULL,
      |FK_USER_ID INT NOT NULL UNIQUE,
      |FOREIGN KEY (FK_USER_ID) REFERENCES USER(ID))""".stripMargin

  protected val dropUserTable = """DROP TABLE USER"""

  protected val dropAddressTable = """DROP TABLE ADDRESS"""

  protected val insertUserRecord = """INSERT INTO USER (LOGIN, PASSWORD) VALUES(?, ?)"""

  protected val selectUserByLogin =
    """SELECT
      |USER.ID, USER.LOGIN, USER.PASSWORD
      |FROM USER WHERE USER.LOGIN = ?""".stripMargin

  protected val insertAddressRecord = """INSERT INTO ADDRESS (STREET, NUMBER, FK_USER_ID) VALUES(?, ?, ?)"""

  protected val selectAddressByUser =
    """SELECT
      |ADDRESS.ID, ADDRESS.STREET, ADDRESS.NUMBER
      |FROM ADDRESS WHERE ADDRESS.FK_USER_ID = ?""".stripMargin
}
