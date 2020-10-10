package ru.rurik.domain.expence.repository
import ru.rurik.domain.expence.Expense
import zio.ZLayer

class ёPostrgresExpenceRepository extends ExpenceRepository.Service {
  override def getById(id: Long): Expense = ???

  override def create(expense: Expense): Expense = ???

  override def getAll: List[Expense] = ???
}

object PostrgresExpenceRepository {

  val layer=ZLayer.apply()

}
