package ru.rurik.domain.expence.repository

import ru.rurik.domain.expence.Expense
import zio.{RIO, Task, ZIO}

object ExpenceRepository {

  trait Service {
    def getById(id: Long): Task[Option[Expense]]

    def getByParentId(id: Long): Task[List[Expense]]

    def create(expense: Expense): Task[Expense]

    def update(id: Long, expense: Expense): Task[Option[Expense]]

    /*
    This method deletes only single expense, not related subExpenses
     */
    def delete(id: Long): Task[Option[Expense]]
  }

  def create(expense: Expense): RIO[ExpenceRepository, Expense] = ZIO.accessM(_.get.create(expense))

  def getById(id: Long): RIO[ExpenceRepository, Option[Expense]] = ZIO.accessM(_.get.getById(id))

  def getByParentId(id: Long): RIO[ExpenceRepository, List[Expense]] = ZIO.accessM(_.get.getByParentId(id))

  def update(id: Long, expense: Expense): RIO[ExpenceRepository, Option[Expense]] = ZIO.accessM(_.get.update(id, expense))

  def delete(id: Long): RIO[ExpenceRepository, Option[Expense]] = ZIO.accessM(_.get.delete(id))

}
