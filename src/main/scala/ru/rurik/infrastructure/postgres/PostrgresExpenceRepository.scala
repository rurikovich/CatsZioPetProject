package ru.rurik.infrastructure.postgres

import ru.rurik.domain.expence.{Expense, ExpenseCategory}
import ru.rurik.domain.expence.repository.ExpenceRepository
import ru.rurik.infrastructure.db.DatabaseProvider
import ru.rurik.infrastructure.db.dbio._
import ru.rurik.infrastructure.postgres.tables.ExpenseTable
import ru.rurik.infrastructure.postgres.tables.ExpenseTable.DbExpense
import slick.lifted.TableQuery
import zio.{Task, ZIO, ZLayer}
import slick.jdbc.PostgresProfile.api._

class PostrgresExpenceRepository(dbProvider: DatabaseProvider) extends ExpenceRepository.Service {

  val expenses = TableQuery[ExpenseTable.Expenses]

  override def getById(id: Long): Task[Option[Expense]] = {
    val query = expenses.filter(_.id === id)
    ZIO.fromDBIO(query.result).provide(dbProvider).map {
      res: Seq[ExpenseTable.DbExpense] =>
        res.headOption.map(
          dbExp => Expense(dbExp.id, dbExp.name, dbExp.category, dbExp.amount, dbExp.parentId)
        )
    }
  }

  override def getByParentId(id: Long): Task[List[Expense]] = {
    val query: Query[ExpenseTable.Expenses, ExpenseTable.DbExpense, Seq] = expenses.filter(_.parentId === id)
    ZIO.fromDBIO(query.result).provide(dbProvider).map {
      _.map(
        dbExp => Expense(dbExp.id, dbExp.name, dbExp.category, dbExp.amount, dbExp.parentId)
      ).toList
    }
  }

  override def create(expense: Expense): Task[Boolean] = {

    val value: DBIOAction[Unit, NoStream, Effect.Write] = DBIO.seq(expenses += DbExpense(11, "name", ExpenseCategory.Food, 1, None))

    ZIO.fromDBIO(value).provide(dbProvider).map(
      _ => true
    )

  }

  override def update(id: Long, expense: Expense): Task[Option[Expense]] = ???

  override def delete(id: Long): Task[Option[Expense]] = ???
}

object PostrgresExpenceRepository {

  val layer: ZLayer[DatabaseProvider, Nothing, ExpenceRepository] =
    ZLayer.fromFunction(new PostrgresExpenceRepository(_))

}
