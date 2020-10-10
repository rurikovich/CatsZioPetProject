package ru.rurik.infrastructure.postgres

import ru.rurik.infrastructure.db.DatabaseProvider
import slick.jdbc.JdbcBackend.Database
import zio.ZIO

trait PostgresDatabaseProvider extends DatabaseProvider {
  override val databaseProvider = new DatabaseProvider.Service {
    override val db = ZIO.effectTotal(
      Database.forURL(
        url = "jdbc:postgresql://localhost/zio?user=postgres",
        driver = "org.postgresql.Driver"
      )
    )
  }
}
