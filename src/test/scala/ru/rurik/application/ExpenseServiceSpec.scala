package ru.rurik.application

import ru.rurik.domain.expence.Expense
import ru.rurik.domain.expence.ExpenseCategory._
import ru.rurik.domain.expence.repository.ExpenceRepository
import ru.rurik.domain.expence.tree.ExpenseTree
import zio.test.Assertion.equalTo
import zio.test._
import zio.{Task, ZLayer}

object ExpenseServiceSpec extends DefaultRunnableSpec {

  val createSuite = suite("ExpenseService") {
    testM("create") {
      assertM(ExpenseService.getExpenseTree(1))(
        equalTo(
          Some(
            ExpenseTree(Expense(Some(1), "name", Food, 1, None),
              Some(List(
                ExpenseTree(Expense(Some(2), "name", Food, 1, Some(1)), Some(List())),
                ExpenseTree(Expense(Some(3), "name", Food, 1, Some(1)), Some(List())))
              ))
          )
        )
      )
    }.provideSomeLayer(TestExpenceRepository.layer)
  }

  val deleteSuite = suite("delete") {
    testM("getExpensesIds") {
      assertM(ExpenseService.getExpensesIds(1))(equalTo(List(1L, 2L, 3L)))
    }.provideSomeLayer(TestExpenceRepository.layer)

    testM("deleteExpenseTree") {
      assertM(ExpenseService.deleteExpenseTree(1))(equalTo(3))
    }.provideSomeLayer(TestExpenceRepository.layer)
  }

  override def spec = suite("All tests")(createSuite, deleteSuite)


}


class TestExpenceRepository extends ExpenceRepository.Service {

  var expenses = List(
    Expense(Some(1), "name", Food, 1, None),
    Expense(Some(2), "name", Food, 1, Some(1)),
    Expense(Some(3), "name", Food, 1, Some(1))
  )

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
  def layer: ZLayer[Any, Nothing, ExpenceRepository] = ZLayer.succeed(new TestExpenceRepository())
}