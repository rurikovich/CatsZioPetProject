package ru.rurik.infrastructure.postgres

import cats.implicits._
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
    ZIO.fromDBIO(query.result).provide(dbProvider).map {
      res: Seq[ExpenseTable.DbExpense] =>
        res.toList.map(
          dbExp => Expense(dbExp.id, dbExp.name, dbExp.amount)
        ).headOption
    }
  }


  def getByIds(ids: List[Long]): Task[Option[List[Expense]]] = {
    val query = expenses.filter(_.id inSet ids)
    ZIO.fromDBIO(query.result).provide(dbProvider).map(_.toList.map(
      dbExp => Expense(dbExp.id, dbExp.name, dbExp.amount)
    ).some)
  }

}

object PostrgresExpenceRepository {

  val layer: ZLayer[DatabaseProvider, Nothing, ExpenceRepository] =
    ZLayer.fromFunction(new PostrgresExpenceRepository(_))

}
