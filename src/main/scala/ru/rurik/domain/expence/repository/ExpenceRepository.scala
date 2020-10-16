package ru.rurik.domain.expence.repository

import ru.rurik.domain.expence.{Expense, ExpenseTree}
import zio.{RIO, Task, ZIO}

object ExpenceRepository {

  trait Service {
    def getById(id: Long): Task[Option[Expense]]

    def getFullExpenseTree(id: Long): Task[Option[ExpenseTree]]
  }

  def getById(id: Long): RIO[ExpenceRepository, Option[Expense]] = ZIO.accessM(_.get.getById(id))

  def getFullExpenseTree(id: Long): RIO[ExpenceRepository, Option[ExpenseTree]] = ZIO.accessM(_.get.getFullExpenseTree(id))

}
