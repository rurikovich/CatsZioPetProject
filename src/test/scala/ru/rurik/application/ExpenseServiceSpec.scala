package ru.rurik.application

import ru.rurik.domain.expence.Expense
import ru.rurik.domain.expence.ExpenseCategory._
import ru.rurik.domain.expence.repository.ExpenceRepository
import ru.rurik.domain.expence.tree.ExpenseTree
import zio.test.Assertion.equalTo
import zio.test._
import zio.{Task, ZLayer}

object ExpenseServiceSpec extends DefaultRunnableSpec {
  override def spec: ZSpec[_root_.zio.test.environment.TestEnvironment, Any] =
    suite("ExpenseService") {

      testM("constructExpenseTree") {

        assertM(ExpenseService.constructExpenseTree(1))(
          equalTo(
            Some(
              ExpenseTree(Expense(1, "name", Food, 1, None),
                Some(List(
                  ExpenseTree(Expense(2, "name", Food, 1, Some(1)), Some(List())),
                  ExpenseTree(Expense(3, "name", Food, 1, Some(1)), Some(List())))
                ))
            )
          )
        )

      }.provideSomeLayer(TestExpenceRepository.layer)

    }


}


class TestExpenceRepository extends ExpenceRepository.Service {

  val expenses = List(
    Expense(1, "name", Food, 1, None),
    Expense(2, "name", Food, 1, Some(1)),
    Expense(3, "name", Food, 1, Some(1))
  )

  //TODO replace by  expression using CATS
  override def getById(id: Long): Task[Option[Expense]] = Task(expenses.find(_.id == id))

  override def getByParentId(id: Long): Task[List[Expense]] = Task(
    expenses.filter(_.parentId.contains(id))
  )

  override def create(expense: Expense): Task[Boolean] =Task{
    true
  }

  override def update(id: Long, expense: Expense): Task[Option[Expense]] = ???

  override def delete(id: Long): Task[Option[Expense]] = ???
}

object TestExpenceRepository {
  val layer: ZLayer[Any, Nothing, ExpenceRepository] = ZLayer.succeed(new TestExpenceRepository())
}