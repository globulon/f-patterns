package fpatterns.web

import fpatterns._
import fpatterns.validation._
import java.net.{ URL, HttpURLConnection, URLConnection }
import scala.language.higherKinds

trait ConnectionStates extends StateTs {
  protected type Connection

  protected type ConnectionState[M[_], A] = StateT[Connection, M, A]

  protected def connectionState[M[_]: Monad, A](f: (Connection) => M[(Connection, A)]): ConnectionState[M, A] =
    StateT[Connection, M, A] { f }
}

trait URLConnectionStates extends ConnectionStates {
  protected type U <: URLConnection

  protected type Connection = U

  protected def makeConnection = kleisi[DomainValidation, URL, Connection] {
    safely { _.openConnection().asInstanceOf[Connection] }
  }
}

trait HttpURLConnectionStates extends URLConnectionStates {
  protected type U = HttpURLConnection

  protected def connect: ConnectionState[DomainValidation, Unit] = connectionState[DomainValidation, Unit] {
    connection => safe(connection.connect()) map { (connection, _) }
  }

  protected def setMethod(verb: String): ConnectionState[DomainValidation, Unit] = connectionState[DomainValidation, Unit] {
    connection => safe(connection.setRequestMethod(verb)) map ((connection, _))
  }
  protected def useInput: ConnectionState[DomainValidation, Unit] = connectionState[DomainValidation, Unit] {
    connection => safe(connection.setDoInput(true)) map ((connection, _))
  }

  protected def setHeader(kv: (String, String)): ConnectionState[DomainValidation, Unit] = connectionState[DomainValidation, Unit] {
    connection => safe(connection.addRequestProperty(kv._1, kv._2)) map { (connection, _) }
  }
}

trait Http extends HttpURLConnectionStates with ConnectionReads with Kleislis {
  private def openGet: Connection => DomainValidation[Connection] = (for {
    _ <- setMethod("GET")
    _ <- useInput
    r <- connect
  } yield r) andThen (_ map (_._1))

  protected def get[A](ra: ReadConnection[DomainValidation, A]): Kleisli[DomainValidation, URL, A] =
    makeConnection >=> openGet >=> ra.run
}
