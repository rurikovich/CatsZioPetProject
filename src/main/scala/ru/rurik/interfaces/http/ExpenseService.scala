package ru.rurik.interfaces.http


import io.circe.{Decoder, Encoder}
import org.http4s.dsl.Http4sDsl
import org.http4s.{EntityDecoder, EntityEncoder, HttpRoutes}
import ru.rurik.application.ExpenseTableService.{ExpenseTable, emptyExpenseTable, expenseTable, toSimplifiedExpenseTable}
import ru.rurik.application.ExpenseTreeService
import ru.rurik.domain.expence.Expense
import ru.rurik.domain.expence.repository.ExpenceRepository
import ru.rurik.domain.expence.tree.ExpenseTree
import ru.rurik.infrastructure.db.DatabaseProvider
import zio.blocking.Blocking
import zio.clock.Clock

import zio._
import zio.interop.catz._
import ru.rurik.domain.expence.ExpenseCategoryJsonCodecs._
import io.circe.generic.auto._
import org.http4s.circe._


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
            ExpenceRepository.create(expense).flatMap(Ok(_))
        }

      case GET -> Root / LongVar(id) =>
        ExpenseTreeService.getExpenseTree(id).flatMap(_.fold(NotFound())(Ok(_)))

      case req@PUT -> Root =>
        req.decode[Expense] {
          expense: Expense =>
            //TODO validate expense has id
            //TODO  redesign to Either[Throwable,Option[Expense]]
            ExpenceRepository.update(expense).flatMap {
              case None => Ok(s"expense id=${expense.id} updated")
              case Some(_) => Ok(s"expense id=${expense.id} inserted")
              case _ => BadRequest(s"expense id=${expense.id}was NOT updated")
            }
        }

      case DELETE -> Root / LongVar(id) =>
        ExpenseTreeService.deleteExpenseTree(id).flatMap {
          case affectedCount: Int if affectedCount > 0 => Ok(s"expense id=$id deleted")
          case _ => BadRequest(s"expense id=$id NOT deleted")
        }

      case GET -> Root / "table" / LongVar(id) =>
        val expenseTableRIO: RIO[ExpenceRepository, ExpenseTable] = for {
          tree: Option[ExpenseTree] <- ExpenseTreeService.getExpenseTree(id)
        } yield tree.map(expenseTable).getOrElse(emptyExpenseTable)

        expenseTableRIO.flatMap {
          table =>
            Ok(toSimplifiedExpenseTable(table))
        }

    }

  }

}
