package ru.rurik.infrastructure.postgres.conf

import ru.rurik.infrastructure.db.DatabaseProvider
import slick.jdbc.JdbcBackend.Database
import zio.{ZIO, ZLayer}

final class PostgresDatabaseProvider extends DatabaseProvider.Service {
  override val db = ZIO.effectTotal(
    Database.forURL(
      //todo  вынести в application.conf
      url = "jdbc:postgresql://localhost/postgres?user=postgres&password=mysecretpassword",
      driver = "org.postgresql.Driver"
    )
  )
}

object PostgresDatabaseProvider {
  val layer: ZLayer[Any, Nothing, DatabaseProvider] = ZLayer.succeed(new PostgresDatabaseProvider())
}
