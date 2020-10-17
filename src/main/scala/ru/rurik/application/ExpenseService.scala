package ru.rurik.application

import cats.implicits.catsSyntaxOptionId
import ru.rurik.domain.expence.Expense
import ru.rurik.domain.expence.repository.ExpenceRepository
import ru.rurik.domain.expence.tree.ExpenseTree
import zio.{RIO, ZIO}

object ExpenseService {

  def constructExpenseTree(id: Long): RIO[ExpenceRepository, Option[ExpenseTree]] = ZIO.accessM[ExpenceRepository](_.get.getById(id)).flatMap {
    case Some(expense) => fetchExpenseTree(expense).map(_.some)
    case None => RIO.succeed[Option[ExpenseTree]](None)
  }

  def fetchExpenseTree(expense: Expense): RIO[ExpenceRepository, ExpenseTree] = expense.subExpenses match {
    case Some(ids) =>
      for {
        leafsOpt: Option[List[Expense]] <- ZIO.accessM[ExpenceRepository](_.get.getByIds(ids))
        expenseTree: ExpenseTree <- fetchExpenseTreeWithSubExpenses(expense, leafsOpt.getOrElse(List.empty[Expense]))
      } yield expenseTree

    case None => RIO(ExpenseTree(expense))
  }


  def fetchExpenseTreeWithSubExpenses(expense: Expense, leafs: List[Expense]): RIO[ExpenceRepository, ExpenseTree] = {
    ZIO.foldLeft(leafs)(List.empty[ExpenseTree]) {
      (list, leaf) => fetchExpenseTree(leaf).map(list ++ List(_))
    }.map {
      expenseTrees => ExpenseTree(expense, expenseTrees.some)
    }
  }

}
