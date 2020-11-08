package ru.rurik.infrastructure.postgres

import ru.rurik.domain.expence.Expense
import ru.rurik.domain.expence.repository.ExpenceRepository
import ru.rurik.infrastructure.db.DatabaseProvider
import ru.rurik.infrastructure.db.dbio._
import ru.rurik.infrastructure.postgres.tables.ExpenseTable
import slick.lifted.TableQuery
import zio.{Task, ZIO, ZLayer}
import slick.jdbc.PostgresProfile.api._

class PostrgresExpenceRepository(dbProvider: DatabaseProvider) extends ExpenceRepository.Service {

  val expenses = TableQuery[ExpenseTable.Expenses]
  val insertQuery = expenses returning expenses.map(_.id) into ((expense, id) => expense.copy(id = id))

  override def getById(id: Long): Task[Option[Expense]] = {
    val query = expenses.filter(_.id === id)
    ZIO.fromDBIO(query.result).provide(dbProvider).map {
      res: Seq[Expense] =>
        res.headOption.map(
          dbExp => Expense(dbExp.id, dbExp.name, dbExp.category, dbExp.amount, dbExp.parentId)
        )
    }
  }

  override def getByParentId(id: Long): Task[List[Expense]] = {
    val query: Query[ExpenseTable.Expenses, Expense, Seq] = expenses.filter(_.parentId === id)
    ZIO.fromDBIO(query.result).provide(dbProvider).map {
      _.map(
        dbExp => Expense(dbExp.id, dbExp.name, dbExp.category, dbExp.amount, dbExp.parentId)
      ).toList
    }
  }

  //TODO redesign to Option[Expense]
  override def create(expense: Expense): Task[Expense] = ZIO.fromDBIO(insertQuery += expense).provide(dbProvider)

  override def update(id: Long, expense: Expense): Task[Option[Expense]] = ???

  override def delete(id: Long): Task[Option[Expense]] = ???
}

object PostrgresExpenceRepository {

  val layer: ZLayer[DatabaseProvider, Nothing, ExpenceRepository] =
    ZLayer.fromFunction(new PostrgresExpenceRepository(_))

}
