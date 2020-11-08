package ru.rurik.infrastructure.postgres.tables

import ru.rurik.domain.expence.{Expense, ExpenseCategory}
import ru.rurik.domain.expence.ExpenseCategory._
import slick.jdbc.PostgresProfile.api._

object ExpenseTable {

  case class LiftedExpense(id: Rep[Option[Long]], name: Rep[String], category: Rep[ExpenseCategory], amount: Rep[Long], parentId: Rep[Option[Long]])

  implicit val expenseCategoryMapper = MappedColumnType.base[ExpenseCategory, String](
    e => e.toString,
    s => ExpenseCategory.withName(s)
  )

  class Expenses(tag: Tag) extends Table[Expense](tag, "expenses") {

    def id = column[Option[Long]]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def category = column[ExpenseCategory]("category")

    def amount = column[Long]("amount")

    def parentId = column[Option[Long]]("parent_id")

    def * = (id, name, category, amount, parentId).<>(Expense.tupled, Expense.unapply)

  }

}
