package ru.rurik.application

import ru.rurik.domain.expence.Expense
import ru.rurik.domain.expence.repository.ExpenceRepository
import ru.rurik.domain.expence.tree.ExpenseTree
import zio.{RIO, ZIO}


object ExpenseTreeService extends ZIOHelper {

  // TODO redesign to @tailrec if possible
  def getExpenseTree(id: Long): RIO[ExpenceRepository, Option[ExpenseTree]] = for {
    expenseOpt: Option[Expense] <- ZIO.accessM[ExpenceRepository](_.get.getById(id))
    subExpenses: List[Expense] <- ZIO.accessM[ExpenceRepository](_.get.getByParentId(id))
    subExpensesRIOList: List[RIO[ExpenceRepository, Option[ExpenseTree]]] = subExpenses.flatMap(_.id).map(getExpenseTree)
    expenseTreeList: List[ExpenseTree] <- collectAllF(subExpensesRIOList)
  } yield expenseOpt.map(ExpenseTree(_, Some(expenseTreeList)))

  def deleteExpenseTree(id: Long): RIO[ExpenceRepository, Int] = for {
    ids <- getExpensesIds(id)
    countOfDeleted <- ZIO.accessM[ExpenceRepository](_.get.delete(ids))
  } yield countOfDeleted

  def getExpensesIds(id: Long): RIO[ExpenceRepository, List[Long]] = for {
    subExpenses: List[Expense] <- ZIO.accessM[ExpenceRepository](_.get.getByParentId(id))
    sudIds: List[Long] <- collectAllF(subExpenses.flatMap(_.id.map(getExpensesIds)))
  } yield id :: sudIds

}