package ru.rurik.infrastructure.postgres

import ru.rurik.infrastructure.db.DatabaseProvider
import slick.jdbc.JdbcBackend.Database
import zio.{ZIO, ZLayer}

final class PostgresDatabaseProvider extends DatabaseProvider.Service {
  override val db = ZIO.effectTotal(
    Database.forURL(
      url = "jdbc:postgresql://localhost/zio?user=postgres",
      driver = "org.postgresql.Driver"
    )
  )
}

object PostgresDatabaseProvider {
  val layer: ZLayer[Any, Nothing, DatabaseProvider] = ZLayer.succeed(new PostgresDatabaseProvider())
}
