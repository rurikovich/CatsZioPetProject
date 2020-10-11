package ru.rurik.infrastructure.db

import slick.basic.BasicBackend
import zio.UIO

object DatabaseProvider {
  trait Service {
    def db: UIO[BasicBackend#DatabaseDef]
  }
}
