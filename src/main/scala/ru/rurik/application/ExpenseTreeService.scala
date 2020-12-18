package ru.rurik.application

import ru.rurik.domain.expence.Expense
import ru.rurik.domain.expence.repository.ExpenceRepository
import ru.rurik.domain.expence.tree.ExpenseTree
import zio.RIO

object ExpenseTreeService extends ZIOHelper {

  // TODO redesign to @tailrec if possible
  def getExpenseTree(id: Long): RIO[ExpenceRepository, Option[ExpenseTree]] =
    for {
      expenseOpt: Option[Expense] <- ExpenceRepository.getById(id)
      subExpenses: List[Expense] <- ExpenceRepository.getByParentId(id)
      expenseTreeList: List[ExpenseTree] <- collectAllF(subExpenses.flatMap(_.id).map(getExpenseTree))
    } yield expenseOpt.map(ExpenseTree(_, Some(expenseTreeList)))

  def deleteExpenseTree(id: Long): RIO[ExpenceRepository, Int] =
    for {
      ids <- getExpensesIds(id)
      countOfDeleted <- ExpenceRepository.delete(ids)
    } yield countOfDeleted

  def getExpensesIds(id: Long): RIO[ExpenceRepository, List[Long]] = for {
    subExpenses <- ExpenceRepository.getByParentId(id)
    ids <- collectAllF(subExpenses.flatMap(_.id.map(getExpensesIds)))
  } yield id :: ids

}