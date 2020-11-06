package ru.rurik.application

import ru.rurik.domain.expence.Expense
import ru.rurik.domain.expence.repository.ExpenceRepository
import ru.rurik.domain.expence.tree.ExpenseTree
import zio.{RIO, ZIO}

object ExpenseService {

  def constructExpenseTree(id: Long): RIO[ExpenceRepository, Option[ExpenseTree]] = for {
    expenseOpt: Option[Expense] <- ZIO.accessM[ExpenceRepository](_.get.getById(id))
    subExpenses: List[Expense] <- ZIO.accessM[ExpenceRepository](_.get.getByParentId(id))
    subExpensesRIOList: List[RIO[ExpenceRepository, Option[ExpenseTree]]] = subExpenses.map(_.id).map(constructExpenseTree)
    expenseTreeList: List[ExpenseTree] <- toRIOList(subExpensesRIOList)
  } yield expenseOpt.map(ExpenseTree(_, Some(expenseTreeList)))


  def toRIOList[R, A](rioList: List[RIO[R, Option[A]]]): RIO[R, List[A]] = {
    val startAcc: RIO[R, List[A]] = RIO.succeed[List[A]](List.empty)
    rioList.foldLeft(startAcc) {
      (rioList: RIO[R, List[A]], rio: RIO[R, Option[A]]) => {
        rioList.zipWith(rio)((list, maybeTree) => list ::: maybeTree.map(List(_)).getOrElse(List.empty))
      }
    }
  }


}
