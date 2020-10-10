package ru.rurik.infrastructure.postgres.tables

import ru.rurik.domain.expence.Expense
import slick.jdbc.H2Profile.api._

object ExpenseTable {

  case class LiftedExpense(id: Rep[Long], amount: Rep[Long])

  implicit object ExpenseShape extends CaseClassShape(LiftedExpense.tupled, Expense.tupled)

  class Expenses(tag: Tag) extends Table[Expense](tag, "EXPENSES") {
    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)

    def amount = column[Long]("amount")

    def * = LiftedExpense(id, amount)
  }

}
