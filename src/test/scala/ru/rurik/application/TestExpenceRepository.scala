package ru.rurik.application

import ru.rurik.domain.expence.Expense
import ru.rurik.domain.expence.repository.ExpenceRepository
import zio.{Task, ZLayer}


class TestExpenceRepository(var expenses: List[Expense]) extends ExpenceRepository.Service {

  //TODO replace by  expression using CATS
  override def getById(id: Long): Task[Option[Expense]] = Task(expenses.find(_.id.exists(_ == id)))

  override def getByParentId(id: Long): Task[List[Expense]] = Task(expenses.filter(_.parentId.contains(id)))

  override def create(expense: Expense): Task[Expense] = Task(expense)

  override def update(expense: Expense): Task[Option[Expense]] = Task(Some(expense))

  override def delete(id: Long): Task[Int] = Task(1)

  override def delete(ids: List[Long]): Task[Int] = Task {
    val count = expenses.count(_.id.exists(id => ids.contains(id)))
    expenses = expenses.filter(_.id.exists(id => !ids.contains(id)))
    count
  }

}

object TestExpenceRepository {
  /*
  def is very important. it creates new layer on every call
   */
  def layer(expenses: List[Expense]): ZLayer[Any, Nothing, ExpenceRepository] = ZLayer.succeed(new TestExpenceRepository(expenses))
}
