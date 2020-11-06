package ru.rurik.domain.expence.repository

import ru.rurik.domain.expence.Expense
import zio.{RIO, Task, ZIO}

object ExpenceRepository {

  trait Service {
    def getById(id: Long): Task[Option[Expense]]

    def getByParentId(id: Long): Task[List[Expense]]
  }

  def getById(id: Long): RIO[ExpenceRepository, Option[Expense]] = ZIO.accessM(_.get.getById(id))

  def getByParentId(id: Long): RIO[ExpenceRepository, List[Expense]] = ZIO.accessM(_.get.getByParentId(id))

}
