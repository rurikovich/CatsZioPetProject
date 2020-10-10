package ru.rurik.infrastructure.postgres

import ru.rurik.domain.expence.Expense
import ru.rurik.domain.expence.repository.ExpenceRepository
import ru.rurik.infrastructure.db.DatabaseProvider
import ru.rurik.infrastructure.postgres.tables.ExpenseTable
import slick.lifted.TableQuery
import zio.ZLayer

trait PostrgresExpenceRepository extends ExpenceRepository.Service  with DatabaseProvider{

  val expenses = TableQuery[ExpenseTable.Expenses]


  override def getById(id: Long): Expense = expenses.

  override def create(expense: Expense): Expense = ???

  override def getAll: List[Expense] = ???
}

object PostrgresExpenceRepository {

  val layer=ZLayer.apply()

}
