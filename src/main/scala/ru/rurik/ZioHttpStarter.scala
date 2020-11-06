package ru.rurik

import cats.effect.ExitCode
import org.http4s.{HttpApp, HttpRoutes}
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.CORS
import ru.rurik.infrastructure.db.DatabaseProvider
import ru.rurik.interfaces.http.ExpenseService
import zio.blocking.Blocking
import zio.clock.Clock
import zio.interop.catz._
import zio.{App, RIO, Runtime, ZEnv, ZIO, logging, ExitCode => ZExitCode}
import fs2.Stream.Compiler._
import org.http4s.implicits._
import ru.rurik.domain.expence.repository.ExpenceRepository

object ZioHttpStarter extends App {
  type AppTask[A] = RIO[ExpenceRepository with DatabaseProvider with Blocking with Clock, A]

  val port = 8080

  override def run(args: List[String]): ZIO[ZEnv, Nothing, ZExitCode] = {
    val prog =
      for {
        _ <- logging.log.info(s"Starting")
        value: HttpRoutes[AppTask] = Router[AppTask]("/reports" -> ExpenseService.routes())
        httpApp = value.orNotFound
        _ <- runHttp(httpApp, port)
      } yield ZExitCode.success

    prog.provideSomeLayer[ZEnv](Layers.live.appLayer).orDie
  }

  def runHttp[R <: Clock](httpApp: HttpApp[RIO[R, *]], port: Int): ZIO[R, Throwable, Unit] = {
    type Task[A] = RIO[R, A]
    ZIO.runtime[R].flatMap {
      implicit rts: Runtime[R] =>
        BlazeServerBuilder
          .apply[Task](rts.platform.executor.asEC)
          .bindHttp(port, "0.0.0.0")
          .withHttpApp(CORS(httpApp))
          .serve
          .compile[Task, Task, ExitCode]
          .drain
    }
  }

}
