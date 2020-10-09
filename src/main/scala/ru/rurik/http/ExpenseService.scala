package ru.rurik.http

import io.circe.Encoder
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import zio._

import zio.interop.catz._

object ExpenseService {


  def routes[R](): HttpRoutes[RIO[R, ?]] = {
    type ExpenseTask[A] = RIO[R, A]

    val dsl: Http4sDsl[ExpenseTask] = Http4sDsl[ExpenseTask]
    import dsl._

    implicit def circeJsonEncoder[A: Encoder]: EntityEncoder[ExpenseTask, A] = jsonEncoderOf[ExpenseTask, A]

    HttpRoutes.of[ExpenseTask] {
      case GET -> Root / LongVar(id) =>
        ZIO.succeed(Some(id)).flatMap {
          _.fold(NotFound())(x => Ok(x))
        }

    }

  }

}
