package ru.rurik.infrastructure.db

import slick.jdbc.JdbcBackend.Database
import zio.ZIO

trait LiveDatabaseProvider extends DatabaseProvider {
  override val databaseProvider = new DatabaseProvider.Service {
    override val db = ZIO.effectTotal(Database.forURL(url = "jdbc:h2:prod"))
  }
}
