package ru.rurik.infrastructure.postgres.tables

import ru.rurik.domain.expence.ExpenseCategory
import ru.rurik.domain.expence.ExpenseCategory._
import slick.jdbc.PostgresProfile.api._

object ExpenseTable {

  case class LiftedExpense(id: Rep[Long], name: Rep[String], category: Rep[ExpenseCategory], amount: Rep[Long], parentId: Rep[Option[Long]])

//  implicit object ExpenseShape extends CaseClassShape(LiftedExpense.tupled, DbExpense.tupled)

  case class DbExpense(id: Long, name: String, category: ExpenseCategory, amount: Long, parentId: Option[Long])





//  // custom case class mapping
//  implicit object BShape extends CaseClassShape(LiftedB.tupled, B.tupled)


  implicit val expenseCategoryMapper = MappedColumnType.base[ExpenseCategory, String](
    e => e.toString,
    s => ExpenseCategory.withName(s)
  )

  class Expenses(tag: Tag) extends Table[DbExpense](tag, "expenses") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def category = column[ExpenseCategory]("category")

    def amount = column[Long]("amount")

    def parentId = column[Option[Long]]("parent_id")

    def * = (id, name, category, amount, parentId).<>(DbExpense.tupled, DbExpense.unapply)

  }

}
