package ru.rurik.application

import ru.rurik.domain.expence.Expense
import ru.rurik.domain.expence.ExpenseCategory.ExpenseCategory
import ru.rurik.domain.expence.tree.{Tree}

object ExpenseTableService {

  type ExpenseTable = Map[ExpenseCategory, Long]

  def emptyExpenseTable: Map[ExpenseCategory, Long] = Map[ExpenseCategory, Long]()

  def toSimplifiedExpenseTable(t: ExpenseTable): Map[String, Long] = t.map {
    case (category, amount) => (category.toString, amount)
  }

  def expenseTable(expenseTree: Tree[Expense]): Map[ExpenseCategory, Long] = {
    import cats.implicits._
    import ru.rurik.domain.expence.tree.Tree._

    expenseTree.foldLeft(Map.empty[ExpenseCategory, Long])(
      (table: Map[ExpenseCategory, Long], expense) => {
        val amount = table.getOrElse[Long](expense.category, 0) + expense.amount
        ((expense.category, amount) :: table.filter(_._1 != expense.category).toList).toMap
      }

    )

  }

  def mergeExpenseTables(t1: ExpenseTable, t2: ExpenseTable): ExpenseTable = {
    (t1.toList ++ t2.toList).groupBy(_._1).map {
      case (category: ExpenseCategory, list: List[(ExpenseCategory, Long)]) =>
        (category, list.map(_._2).sum)
    }
  }


}
