package ru.rurik.infrastructure.postgres.tables

import slick.jdbc.PostgresProfile.api._

object ExpenseTable {

  case class LiftedExpense(id: Rep[Long], name: Rep[String], amount: Rep[Long], parentId: Rep[Option[Long]])

  implicit object ExpenseShape extends CaseClassShape(LiftedExpense.tupled, DbExpense.tupled)

  case class DbExpense(id: Long, name: String, amount: Long, parentId: Option[Long])


  class Expenses(tag: Tag) extends Table[DbExpense](tag, "expenses") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def amount = column[Long]("amount")

    def parentId = column[Option[Long]]("parent_id")

    def * = LiftedExpense(id, name, amount, parentId)

  }

}
