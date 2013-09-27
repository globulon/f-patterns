package fpatterns.web

import fpatterns.{ Monad, ReaderT }
import fpatterns.validation._
import java.net.{ HttpURLConnection, URLConnection }
import scala.language.higherKinds

trait ConnectionReads {
  protected type Connection

  protected type ReadConnection[M[_], A] = ReaderT[M, Connection, A]

  protected def readConnection[M[_]: Monad, A](f: (Connection) => M[A]): ReaderT[M, Connection, A] =
    ReaderT[M, Connection, A] { f }
}

trait URLConnectionReads extends ConnectionReads {
  protected type U <: URLConnection

  protected type Connection = U
}

trait HttpURLConnectionReads extends URLConnectionReads {
  protected type U = HttpURLConnection

  protected def httpStatus: ReadConnection[DomainValidation, Int] = readConnection[DomainValidation, Int] {
    safely { _.getResponseCode }
  }
}
