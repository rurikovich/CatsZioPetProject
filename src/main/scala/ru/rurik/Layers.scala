package ru.rurik

import ru.rurik.domain.expence.repository.{ExpenceRepository, PostrgresExpenceRepository}
import ru.rurik.expence.repository.ExpenceRepository
import zio.ZLayer
import zio.blocking.Blocking
import zio.logging.Logging
import zio.logging.slf4j.Slf4jLogger

object Layers {

  type Layer0Env = ExpenceRepository with Blocking with Logging

  object live {

    val layer: ZLayer[Blocking, Throwable, Layer0Env] =
      PostrgresExpenceRepository.layer ++ Blocking.any ++ Slf4jLogger.make((_, msg) => msg)
  }

}
