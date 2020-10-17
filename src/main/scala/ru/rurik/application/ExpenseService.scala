package ru.rurik.application

import cats.implicits.catsSyntaxOptionId
import ru.rurik.application.ExpenseService.fetchExpenseTree
import ru.rurik.domain.expence.Expense
import ru.rurik.domain.expence.repository.ExpenceRepository
import ru.rurik.domain.expence.tree.{ExpenseTree, Tree}
import zio.{RIO, Task, UIO, ZIO}

object ExpenseService {

  def constructExpenseTree(id: Long): RIO[ExpenceRepository, Option[ExpenseTree]] = {

    for {
      root <- ZIO.accessM[ExpenceRepository](_.get.getById(id))


    } yield root

  }

  def fetchExpenseTree(expense: Expense): RIO[ExpenceRepository, Option[ExpenseTree]] = {

    expense.subExpenses match {
      case Some(ids) =>
        val leafsRIO: RIO[ExpenceRepository, Option[List[Expense]]] = ZIO.accessM[ExpenceRepository](_.get.getByIds(ids))

        leafsRIO.flatMap {
          leafsOpt: Option[List[Expense]] =>
            val option: Option[RIO[ExpenceRepository, ExpenseTree]] = leafsOpt.map {
              leafs: List[Expense] =>

                val value: RIO[ExpenceRepository, ExpenseTree] = ZIO.foldLeft(leafs)(List.empty[Option[ExpenseTree]]) {
                  (list: List[Option[ExpenseTree]], leaf: Expense) =>
                    fetchExpenseTree(leaf).map {
                      expTree: Option[ExpenseTree] => list ++ List(expTree)
                    }
                }.map {
                  expenseTrees: List[Option[ExpenseTree]] =>
                    ExpenseTree(expense, expenseTrees.flatten.some)
                }
                value


            }
            option


        }


      case None => RIO(Some(ExpenseTree(expense)))
    }


  }


}
