package ru.rurik.interfaces.http

import org.http4s.{EntityDecoder, EntityEncoder, HttpRoutes}
import ru.rurik.domain.expence.repository.ExpenceRepository
import ru.rurik.domain.expence.repository.ExpenceRepository._
import ru.rurik.infrastructure.db.DatabaseProvider
import zio.blocking.Blocking
import zio.clock.Clock
import io.circe.{Decoder, Encoder}
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import ru.rurik.domain.expence.Expense
import zio._
import zio.interop.catz._


object ExpenseService {

  def routes[R <: ExpenceRepository with DatabaseProvider with Blocking with Clock](): HttpRoutes[RIO[R, ?]] = {
    type ExpenseTask[A] = RIO[R, A]

    val dsl: Http4sDsl[ExpenseTask] = Http4sDsl[ExpenseTask]
    import dsl._

    implicit def circeJsonDecoder[A: Decoder]: EntityDecoder[ExpenseTask, A] = jsonOf[ExpenseTask, A]

    implicit def circeJsonEncoder[A: Encoder]: EntityEncoder[ExpenseTask, A] = jsonEncoderOf[ExpenseTask, A]


    HttpRoutes.of[ExpenseTask] {

      case req@POST -> Root =>
        req.decode[Expense] {
          expense: Expense =>
            ExpenceRepository.create(expense).flatMap(
              _ => Ok("ss")
            )
        }


      case GET -> Root / LongVar(id) =>
        ExpenceRepository.getById(id).flatMap((r: Option[Expense]) => r.fold(NotFound())(x => Ok(x.toString)))

      case PUT -> Root / LongVar(id) =>
        getById(id).flatMap(
          _.fold(NotFound())(x => Ok(x.toString))
        )

      case DELETE -> Root / LongVar(id) =>
        getById(id).flatMap(_.fold(NotFound())(x => Ok(x.toString)))

    }

  }

}
