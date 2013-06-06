package fpatterns.web

import fpatterns.{ Monoids, Lens, Endo }

case class HttpResponse(body: String, code: Int, headers: Map[String, Iterable[Any]])

trait ResponseSelectors {
  def code: Lens[HttpResponse, Int] = Lens(
    g = _.code,
    s = (r, c) => r.copy(code = c))
}

trait ResponseCombinators {
  self: ResponseSelectors with Monoids =>
  protected type TransformResponse = HttpResponse => HttpResponse

  implicit private class RichTransformResponse(override val run: TransformResponse) extends Endo[HttpResponse]

  def status(code: Int): TransformResponse = _.copy(code = code)

  def ok: TransformResponse = status(200)

  def header[A](k: String): A => TransformResponse =
    (v) => (r) => r.copy(headers = r.headers + (k -> Seq(v)))

  def contentType: String => TransformResponse = header[String]("content-type")

  def applicationJson = contentType("application/json")

  def contentLength: Long => TransformResponse = header[Long]("content-length")

  def jsonOk(json: String) = ok |+| applicationJson |+| contentLength(json.getBytes.length)
}
