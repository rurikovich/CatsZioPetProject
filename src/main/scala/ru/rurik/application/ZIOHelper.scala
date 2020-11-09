package ru.rurik.application

import zio.{RIO, ZIO}

trait ZIOHelper {

  def toListOfZIO[R, E, A](rioList: List[ZIO[R, E, Option[A]]]): ZIO[R, E, List[A]] = {
    val startAcc: ZIO[R, E, List[A]] = RIO.succeed[List[A]](List.empty)
    rioList.foldLeft(startAcc) {
      (rioList: ZIO[R, E, List[A]], rio: ZIO[R, E, Option[A]]) => {
        rioList.zipWith(rio)((list, maybeTree) => list ::: maybeTree.map(List(_)).getOrElse(List.empty))
      }
    }
  }

  def zioListFlatten[R, E, A](zioList: List[ZIO[R, E, List[A]]]): ZIO[R, E, List[A]] = {
    val start: ZIO[R, E, List[A]] = ZIO.succeed[List[A]](List.empty[A])
    zioList.foldLeft(start)((acc, task) => acc.zipWith(task)(_ ::: _))
  }

}
