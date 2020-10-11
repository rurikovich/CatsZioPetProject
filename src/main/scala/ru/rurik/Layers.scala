package ru.rurik

import ru.rurik.domain.expence.repository.ExpenceRepository
import ru.rurik.infrastructure.db.DatabaseProvider
import ru.rurik.infrastructure.postgres.{PostgresDatabaseProvider, PostrgresExpenceRepository}
import zio.ZLayer
import zio.blocking.Blocking
import zio.logging.Logging
import zio.logging.slf4j.Slf4jLogger

object Layers {

  type Layer0Env = DatabaseProvider with ExpenceRepository with Blocking with Logging

  object live {

    val layer: ZLayer[DatabaseProvider with Blocking, Throwable, Layer0Env] =
      PostrgresExpenceRepository.layer ++
        PostgresDatabaseProvider.layer ++
        Blocking.live ++
        Slf4jLogger.make((_, msg) => msg)
  }

}
