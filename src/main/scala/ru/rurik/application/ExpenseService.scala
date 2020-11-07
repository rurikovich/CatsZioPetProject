package ru.rurik.application

import ru.rurik.domain.expence.Expense
import ru.rurik.domain.expence.repository.ExpenceRepository
import ru.rurik.domain.expence.tree.ExpenseTree
import zio.{RIO, ZIO}

object ExpenseService extends ZIOHelper {

  // TODO redisign to @tailrec if possible
  def constructExpenseTree(id: Long): RIO[ExpenceRepository, Option[ExpenseTree]] = for {
    expenseOpt: Option[Expense] <- ZIO.accessM[ExpenceRepository](_.get.getById(id))
    subExpenses: List[Expense] <- ZIO.accessM[ExpenceRepository](_.get.getByParentId(id))
    subExpensesRIOList: List[RIO[ExpenceRepository, Option[ExpenseTree]]] = subExpenses.map(_.id).map(constructExpenseTree)
    expenseTreeList: List[ExpenseTree] <- toListOfZIO(subExpensesRIOList)
  } yield expenseOpt.map(ExpenseTree(_, Some(expenseTreeList)))

}
