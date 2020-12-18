package ru.rurik.infrastructure.db

import slick.basic.BasicBackend
import zio.{RIO, UIO, ZIO}

object DatabaseProvider {

  trait Service {
    def db: UIO[BasicBackend#DatabaseDef]
  }

  def db: RIO[DatabaseProvider, BasicBackend#DatabaseDef] = ZIO.accessM(_.get.db)

}
