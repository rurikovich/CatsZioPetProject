package ru.rurik.interfaces.http

import org.http4s.{EntityDecoder, HttpRoutes}
import ru.rurik.domain.expence.repository.ExpenceRepository
import ru.rurik.domain.expence.repository.ExpenceRepository._
import ru.rurik.infrastructure.db.DatabaseProvider
import zio.blocking.Blocking
import zio.clock.Clock
import io.circe.{ Decoder }
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import zio._
import zio.interop.catz._


object ExpenseService {

  def routes[R <: ExpenceRepository with DatabaseProvider with Blocking with Clock](): HttpRoutes[RIO[R, ?]] = {
    type ExpenseTask[A] = RIO[R, A]

    val dsl: Http4sDsl[ExpenseTask] = Http4sDsl[ExpenseTask]
    import dsl._

    implicit def circeJsonDecoder[A: Decoder]: EntityDecoder[ExpenseTask, A] = jsonOf[ExpenseTask, A]


    HttpRoutes.of[ExpenseTask] {

      case req@POST -> Root =>
        req.decode[ExpenseExternalDto] {
          data: ExpenseExternalDto =>
            Ok(data.toString)
        }


      case GET -> Root / LongVar(id) =>
        ExpenceRepository.getById(id).flatMap(_.fold(NotFound())(x => Ok(x.toString)))

      case PUT -> Root / LongVar(id) =>
        getById(id).flatMap(_.fold(NotFound())(x => Ok(x.toString)))

      case DELETE -> Root / LongVar(id) =>
        getById(id).flatMap(_.fold(NotFound())(x => Ok(x.toString)))

    }

  }

}
