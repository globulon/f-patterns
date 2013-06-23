package fpatterns.db

import fpatterns.async.{AsyncContext, Async, Par}
import fpatterns.validation._

trait PersistenceContext extends TransactionManager with DB {
  protected def provideConnection: ProvideConnection

  protected def transact[A](dba: DBResult[A]): DomainValidation[A] = provideConnection(makeTx(dba))

  protected def readOnly[A](dba: DBResult[A]): DomainValidation[A] = provideConnection(dba)
}

trait AsyncPersistenceContext extends PersistenceContext {
  self: AsyncContext =>

  protected def pTransact[A]: (DBResult[A]) => Par[DomainValidation[A]] = makeAsync(transact)

  protected def pReadOnly[A]: (DBResult[A]) => Par[DomainValidation[A]] = makeAsync(readOnly)
}