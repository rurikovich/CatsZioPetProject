package ru.rurik.domain.expence.repository

import ru.rurik.domain.expence.{Expense}
import zio.{RIO, Task, ZIO}

object ExpenceRepository {

  trait Service {

    def getById(id: Long): Task[Option[Expense]]

    def getByIds(ids: List[Long]): Task[Option[List[Expense]]]

  }

  def getById(id: Long): RIO[ExpenceRepository, Option[Expense]] = ZIO.accessM(_.get.getById(id))

  def getByIds(ids: List[Long]): RIO[ExpenceRepository, Option[List[Expense]]] = ZIO.accessM(_.get.getByIds(ids))

}
