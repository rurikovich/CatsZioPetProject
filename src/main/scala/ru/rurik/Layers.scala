package ru.rurik

import ru.rurik.domain.expence.repository.ExpenceRepository
import ru.rurik.infrastructure.db.DatabaseProvider
import ru.rurik.infrastructure.postgres.PostrgresExpenceRepository
import ru.rurik.infrastructure.postgres.conf.PostgresDatabaseProvider
import zio.ZLayer
import zio.blocking.Blocking
import zio.logging.Logging
import zio.logging.slf4j.Slf4jLogger

object Layers {

  type Layer0Env = Blocking with Logging

  type Layer1Env = Layer0Env with DatabaseProvider

  type Layer2Env = Layer1Env with ExpenceRepository

  type AppEnv = Layer2Env

  object live {

    val layer0: ZLayer[Blocking, Throwable, Layer0Env] =
      Blocking.any ++ Slf4jLogger.make((_, msg) => msg)

    val layer1: ZLayer[Layer0Env, Throwable, Layer1Env] =
      PostgresDatabaseProvider.layer ++ ZLayer.identity

    val layer2: ZLayer[Layer1Env, Throwable, Layer2Env] =
      PostrgresExpenceRepository.layer ++ ZLayer.identity

    val appLayer: ZLayer[Blocking, Throwable, AppEnv] = layer0 >>> layer1 >>> layer2

  }

}
