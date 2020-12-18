package ru.rurik.application

import zio.ZIO

trait ZIOHelper {

  def collectAllF[R, E, A](zioList: List[ZIO[R, E, IterableOnce[A]]]): ZIO[R, E, List[A]] =
    ZIO.collectAll(zioList).map(_.flatten)

}
