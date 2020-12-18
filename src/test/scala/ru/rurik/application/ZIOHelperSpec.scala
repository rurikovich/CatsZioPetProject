package ru.rurik.application


import zio.ZIO
import zio.test.Assertion.equalTo
import zio.test.{DefaultRunnableSpec, ZSpec, assertM, suite, testM}

object ZIOHelperSpec extends DefaultRunnableSpec with ZIOHelper {

  type R = _root_.zio.test.environment.TestEnvironment
  type E = Throwable
  type A = Int

  val toListOfZIOSuite = suite("toListOfZIO") {
    testM("toListOfZIO") {
      val list = (0 to 10).toList
      val rioList: List[ZIO[R, E, Option[A]]] = list.map(i => ZIO.succeed(Some(i)))
      assertM(collectAllF[R, E, A](rioList))(equalTo(list))
    }

    testM("toListOfZIO with None") {
      val rioList: List[ZIO[R, E, Option[A]]] = List(
        ZIO.succeed(Some(1)),
        ZIO.succeed(Some(2)),
        ZIO.succeed(None),
        ZIO.succeed(Some(3))
      )

      assertM(collectAllF[R, E, A](rioList))(equalTo(List(1,2,3)))
    }
  }

  val zioListFlattenSuite = suite("zioListFlatten") {
    testM("zioListFlatten") {
      val list: List[List[A]] = List((0 to 3).toList, (3 to 6).toList, (6 to 9).toList)
      val rioList: List[ZIO[R, E, List[A]]] = list.map(ll => ZIO.succeed(ll))
      assertM(collectAllF[R, E, A](rioList))(equalTo(list.flatten))
    }
  }


  override def spec: ZSpec[_root_.zio.test.environment.TestEnvironment, Any] = suite("All tests")(toListOfZIOSuite, zioListFlattenSuite)

}
