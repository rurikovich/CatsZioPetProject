package ru.rurik.domain.expence.repository

import ru.rurik.domain.expence.Expense
import zio.{RIO, Task, ZIO}

object ExpenceRepository {

  trait Service {
    def getById(id: Long): Task[Option[Expense]]

    def getByParentId(id: Long): Task[List[Expense]]

    def create(expense: Expense): Task[Expense]

    def update(expense: Expense): Task[Option[Expense]]

    /*
    This method deletes only single expense, not related subExpenses
    return count of affected rows
     */
    def delete(id: Long): Task[Int]

    def delete(ids: List[Long]): Task[Int]

  }

  def create(expense: Expense): RIO[ExpenceRepository, Expense] = ZIO.accessM(_.get.create(expense))

  def getById(id: Long): RIO[ExpenceRepository, Option[Expense]] = ZIO.accessM(_.get.getById(id))

  def getByParentId(id: Long): RIO[ExpenceRepository, List[Expense]] = ZIO.accessM(_.get.getByParentId(id))

  def update(expense: Expense): RIO[ExpenceRepository, Option[Expense]] = ZIO.accessM(_.get.update(expense))

  def delete(id: Long): RIO[ExpenceRepository, Int] = ZIO.accessM(_.get.delete(id))

  def delete(ids: List[Long]): RIO[ExpenceRepository, Int] = ZIO.accessM(_.get.delete(ids))

}
