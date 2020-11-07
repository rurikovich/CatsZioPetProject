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

}
