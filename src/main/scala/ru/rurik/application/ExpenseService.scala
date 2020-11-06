package ru.rurik.application

import ru.rurik.domain.expence.Expense
import ru.rurik.domain.expence.repository.ExpenceRepository
import zio.{RIO, ZIO}

object ExpenseService {

  def testGetById(): RIO[ExpenceRepository, Option[Expense]] = ZIO.accessM[ExpenceRepository](_.get.getById(1))

}
