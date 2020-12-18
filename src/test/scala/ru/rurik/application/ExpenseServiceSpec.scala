package ru.rurik.application


import ru.rurik.application.ExpenseTableService.{ExpenseTable, expenseTable}
import ru.rurik.application.ExpenseTreeService.{deleteExpenseTree, getExpenseTree, getExpensesIds}
import ru.rurik.domain.expence.Expense
import ru.rurik.domain.expence.ExpenseCategory._
import ru.rurik.domain.expence.tree.ExpenseTree
import zio.test.Assertion.equalTo
import zio.test._

object ExpenseServiceSpec extends DefaultRunnableSpec {

  val expenses: List[Expense] = List(
    Expense(Some(1), "name", Food, 1, None),
    Expense(Some(2), "name", Food, 1, Some(1)),
    Expense(Some(3), "name", Food, 1, Some(1))
  )

  val tree: ExpenseTree = ExpenseTree(
    Expense(Some(1), "name", Food, 1, None),
    Some(List(
      ExpenseTree(Expense(Some(2), "name", Food, 1, Some(1)), Some(List())),
      ExpenseTree(Expense(Some(3), "name", Food, 1, Some(1)), Some(List()))
    ))
  )

  val treeExpenseTable: ExpenseTable = Map(Food -> 3)

  val t: ExpenseTable = Map(Food -> 3L, Appliances -> 4L)
  val t1: ExpenseTable = Map(Food -> 1L)
  val t2: ExpenseTable = Map(Food -> 2L, Appliances -> 4L)


  val expenseTreeSuite = suite("ExpenseTree")(

    testM("getExpenseTree") {
      assertM(getExpenseTree(1))(equalTo(Some(tree)))
    }.provideSomeLayer(TestExpenceRepository.layer(expenses)),

    testM("getExpensesIds") {
      assertM(getExpensesIds(1))(equalTo(List(1L, 2L, 3L)))
    }.provideSomeLayer(TestExpenceRepository.layer(expenses)),

    testM("deleteExpenseTree") {
      assertM(deleteExpenseTree(1))(equalTo(3))
    }.provideSomeLayer(TestExpenceRepository.layer(expenses))

  )

  val expenseTableSuite = suite("ExpenseTable")(


    test("expenseTable") {
      assert(expenseTable(tree))(equalTo(treeExpenseTable))
    }

  )

  override def spec: ZSpec[environment.TestEnvironment, Any] = suite("All tests")(
    expenseTreeSuite,
    expenseTableSuite
  )

}

