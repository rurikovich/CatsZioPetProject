package ru.rurik.infrastructure

import ru.rurik.infrastructure.db.DatabaseProvider
import slick.jdbc.H2Profile.backend._
import zio.ZIO

trait PostgresDatabaseProvider extends DatabaseProvider {
  override val databaseProvider = new DatabaseProvider.Service {
    override val db = ZIO.effectTotal(
      Database.forURL("jdbc:postgresql://localhost/zio?user=postgres", driver = "org.postgresql.Driver")
    )
  }
}
