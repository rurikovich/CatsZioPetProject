package ru.rurik.domain.expence.repository

import ru.rurik.domain.expence.Expense
import zio.{IO, Task}

object ExpenceRepository {

  trait Service {
    def getById(id: Long): Task[Option[Expense]]

    def add(expense: Expense): Task[Long]

    def getAll: Task[List[Expense]]
  }

}
