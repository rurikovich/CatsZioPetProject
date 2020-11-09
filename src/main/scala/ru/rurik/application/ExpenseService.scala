package ru.rurik.application

import ru.rurik.domain.expence.Expense
import ru.rurik.domain.expence.ExpenseCategory.ExpenseCategory
import ru.rurik.domain.expence.repository.ExpenceRepository
import ru.rurik.domain.expence.tree.ExpenseTree
import zio.{RIO, Task, ZIO}

object ExpenseService extends ZIOHelper {

  // TODO redisign to @tailrec if possible
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


  def zioListFlatten[R, E, A](zioList: List[ZIO[R, E, List[A]]]): ZIO[R, E, List[A]] = {
    val start: ZIO[R, E, List[A]] = ZIO.succeed[List[A]](List.empty[A])
    zioList.foldLeft(start)((acc, task) => acc.zipWith(task)(_ ::: _))
  }


  def expenseTable(expenseTree: ExpenseTree): Task[Map[ExpenseCategory, Long]] = ???


}