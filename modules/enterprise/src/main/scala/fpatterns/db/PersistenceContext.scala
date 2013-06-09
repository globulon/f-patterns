package fpatterns.db

import fpatterns.validation._

trait PersistenceContext extends TransactionManager with DB {
  protected def provider: ConnectionProvider

  protected def inTx[A](dba: DBResult[A]): DomainValidation[A] = provider(makeTx(dba))

  protected def readOnly[A](dba: DBResult[A]): DomainValidation[A] = provider(dba)
}
