package ru.rurik.domain.expence

object ExpenseCategory extends Enumeration {
  type ExpenseCategory = Value
  val Food = Value("продукты")
  val Appliances = Value("бытовая техника")
  val Services = Value("услуги")
  val Other = Value("прочее")
}

import  ExpenseCategory._

case class Expense(id: Long, name: String, category: ExpenseCategory, amount: Long, parentId: Option[Long] = None)



