package ru.rurik.domain.expence.repository

import ru.rurik.domain.expence.Expense
import zio.Task

object ExpenceRepository {

  trait Service {
    def getById(id: Long): Task[Option[Expense]]
  }

}
