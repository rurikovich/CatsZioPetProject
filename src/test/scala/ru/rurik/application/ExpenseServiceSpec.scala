package ru.rurik.application

import ru.rurik.domain.expence.Expense
import ru.rurik.domain.expence.repository.ExpenceRepository
import zio.test.Assertion.equalTo
import zio.test._
import zio.{Task, ZLayer}

object ExpenseServiceSpec extends DefaultRunnableSpec {
  override def spec: ZSpec[_root_.zio.test.environment.TestEnvironment, Any] =
    suite("ExpenseService") {

      testM("constructExpenseTree") {

        assertM(ExpenseService.testGetById())(equalTo(Some(Expense(1, "name", 1, None))))

      }.provideSomeLayer(TestExpenceRepository.layer)

    }


}


class TestExpenceRepository extends ExpenceRepository.Service {
  override def getById(id: Long): Task[Option[Expense]] = Task(Some(Expense(1, "name", 1, None)))

}

object TestExpenceRepository {
  val layer: ZLayer[Any, Nothing, ExpenceRepository] = ZLayer.succeed(new TestExpenceRepository())
}