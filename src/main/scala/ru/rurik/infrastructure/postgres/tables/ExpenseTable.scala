package ru.rurik.infrastructure.postgres.tables

import slick.jdbc.PostgresProfile.api._

object ExpenseTable {

  case class LiftedExpense(id: Rep[Long], name: Rep[String], amount: Rep[Long])

  implicit object ExpenseShape extends CaseClassShape(LiftedExpense.tupled, DbExpense.tupled)

  case class DbExpense(id: Long, name: String, amount: Long)


  class Expenses(tag: Tag) extends Table[DbExpense](tag, "EXPENSES") {

    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)

    def name = column[String]("NAME")

    def amount = column[Long]("AMOUNT")

    def * = LiftedExpense(id, name, amount)

  }

}
