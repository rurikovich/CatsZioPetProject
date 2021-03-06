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
    ZIO.fromDBIO(query.result).provide(dbProvider).map(_.headOption)
  }

  override def getByParentId(id: Long): Task[List[Expense]] = {
    val query = expenses.filter(_.parentId === id)
    ZIO.fromDBIO(query.result).provide(dbProvider).map(_.toList)
  }

  //TODO redesign to Option[Expense]
  override def create(expense: Expense): Task[Expense] = ZIO.fromDBIO(insertQuery += expense).provide(dbProvider)

  override def update(expense: Expense): Task[Option[Expense]] = ZIO.fromDBIO(
    (expenses returning expenses).insertOrUpdate(expense)
  ).provide(dbProvider)

  /*
  return count of affected rows
   */
  override def delete(id: Long): Task[Int] = ZIO.fromDBIO(expenses.filter(_.id === id).delete).provide(dbProvider)

  override def delete(ids: List[Long]): Task[Int] = ZIO.fromDBIO(expenses.filter(_.id.inSet(ids)).delete).provide(dbProvider)

}

object PostrgresExpenceRepository {

  val layer: ZLayer[DatabaseProvider, Nothing, ExpenceRepository] =
    ZLayer.fromFunction(new PostrgresExpenceRepository(_))

}
