package ru.rurik.infrastructure.db

import slick.dbio.DBIO
import zio.ZIO

object dbio {

  implicit class ZIOObjOps(private val obj: ZIO.type) extends AnyVal {
    def fromDBIO[R](dbio: DBIO[R]): ZIO[DatabaseProvider, Throwable, R] =
      for {
        db <- DatabaseProvider.db
        r <- ZIO.fromFuture(_ => db.run(dbio))
      } yield r
  }

}
