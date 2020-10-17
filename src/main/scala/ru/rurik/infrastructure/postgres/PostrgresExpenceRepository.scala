package ru.rurik.infrastructure.postgres

import cats.implicits._
import cats.data.Nested
import ru.rurik.domain.expence.{Expense, ExpenseTree}
import ru.rurik.domain.expence.repository.ExpenceRepository
import ru.rurik.infrastructure.db.DatabaseProvider
import ru.rurik.infrastructure.db.dbio._
import ru.rurik.infrastructure.postgres.tables.ExpenseTable
import slick.jdbc.PostgresProfile.api._
import slick.lifted.TableQuery
import zio.{Task, ZIO, ZLayer}

class PostrgresExpenceRepository(dbProvider: DatabaseProvider) extends ExpenceRepository.Service {

  val expenses = TableQuery[ExpenseTable.Expenses]

  override def getById(id: Long): Task[Option[Expense]] = {
    val query = expenses.filter(_.id === id)
    ZIO.fromDBIO(query.result).provide(dbProvider).map {
      res =>
        res.toList.headOption
    }
  }


  def getByIds(ids: List[Long]): Task[Option[List[Expense]]] = {
    val query = expenses.filter(_.id inSet ids)
    ZIO.fromDBIO(query.result).provide(dbProvider).map(_.toList.some)
  }

}

object PostrgresExpenceRepository {

  val layer: ZLayer[DatabaseProvider, Nothing, ExpenceRepository] =
    ZLayer.fromFunction(new PostrgresExpenceRepository(_))

}
