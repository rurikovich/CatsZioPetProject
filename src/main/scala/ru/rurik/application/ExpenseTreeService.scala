package ru.rurik.application

import ru.rurik.domain.expence.Expense
import ru.rurik.domain.expence.ExpenseCategory.ExpenseCategory
import ru.rurik.domain.expence.repository.ExpenceRepository
import ru.rurik.domain.expence.tree.ExpenseTree
import zio.{RIO, Task, ZIO}


object ExpenseTreeService extends ZIOHelper {
  type ExpenseTable = Map[ExpenseCategory, Long]

  def EmptyExpenseTable: Map[ExpenseCategory, Long] = Map[ExpenseCategory, Long]()

  def toSimplifiedExpenseTable(t: ExpenseTable): Map[String, Long] = t.map {
    case (category, amount) => (category.toString, amount)
  }

  // TODO redesign to @tailrec if possible
  def getExpenseTree(id: Long): RIO[ExpenceRepository, Option[ExpenseTree]] = for {
    expenseOpt: Option[Expense] <- ZIO.accessM[ExpenceRepository](_.get.getById(id))
    subExpenses: List[Expense] <- ZIO.accessM[ExpenceRepository](_.get.getByParentId(id))
    subExpensesRIOList: List[RIO[ExpenceRepository, Option[ExpenseTree]]] = subExpenses.flatMap(_.id).map(getExpenseTree)
    expenseTreeList: List[ExpenseTree] <- toListOfZIO(subExpensesRIOList)
  } yield expenseOpt.map(ExpenseTree(_, Some(expenseTreeList)))

  def deleteExpenseTree(id: Long): RIO[ExpenceRepository, Int] = for {
    ids <- getExpensesIds(id)
    countOfDeleted <- ZIO.accessM[ExpenceRepository](_.get.delete(ids))
  } yield countOfDeleted

  def getExpensesIds(id: Long): RIO[ExpenceRepository, List[Long]] = for {
    subExpenses: List[Expense] <- ZIO.accessM[ExpenceRepository](_.get.getByParentId(id))
    sudIds: List[Long] <- zioListFlatten(subExpenses.flatMap(_.id.map(getExpensesIds)))
  } yield List(id) ::: sudIds

  def expenseTable(expenseTree: ExpenseTree): Task[ExpenseTable] = {
    val value = expenseTree.value
    val tTask: Task[ExpenseTable] = Task.succeed(Map(value.category -> value.amount))
    expenseTree.leafs match {
      case Some(leafs) => leafs.foldLeft(tTask) {
        (t1Task, t2) => mergeExpenseTablesTasks(t1Task, expenseTable(t2))
      }
      case None => tTask
    }
  }

  def mergeExpenseTablesTasks(t1Task: Task[ExpenseTable], t2Task: Task[ExpenseTable]): Task[ExpenseTable] = for {
    t1 <- t1Task
    t2 <- t2Task
  } yield mergeExpenseTables(t1, t2)

  def mergeExpenseTables(t1: ExpenseTable, t2: ExpenseTable): ExpenseTable = {
    (t1.toList ++ t2.toList).groupBy(_._1).map {
      case (category: ExpenseCategory, list: List[(ExpenseCategory, Long)]) =>
        (category, list.map(_._2).sum)
    }
  }

}