package ru.rurik.infrastructure.postgres

import ru.rurik.domain.expence.Expense
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
    ZIO.fromDBIO(query.result).provide(dbProvider).map(_.toList.headOption)
  }

}

object PostrgresExpenceRepository {

  val layer: ZLayer[DatabaseProvider, Nothing, ExpenceRepository] =
    ZLayer.fromFunction(new PostrgresExpenceRepository(_))

}