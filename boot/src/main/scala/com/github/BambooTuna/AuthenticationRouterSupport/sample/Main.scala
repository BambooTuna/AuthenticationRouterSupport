package com.github.BambooTuna.AuthenticationRouterSupport.sample

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import cats.effect.{Blocker, Resource}
import com.typesafe.config.{Config, ConfigFactory}
import doobie.hikari.HikariTransactor
import monix.eval.Task

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}

object Main extends App {

  val rootConfig: Config = ConfigFactory.load()

  implicit val system: ActorSystem =
    ActorSystem("AuthenticationRouterSupport", config = rootConfig)
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val ec: ExecutionContext = monix.execution.Scheduler.Implicits.global
  val jdbcClient: Resource[Task, HikariTransactor[Task]] =
    HikariTransactor.newHikariTransactor[Task](
      system.settings.config.getString("slick.db.driver"),
      system.settings.config.getString("slick.db.url"),
      system.settings.config.getString("slick.db.user"),
      system.settings.config.getString("slick.db.password"),
      ec,
      Blocker.liftExecutionContext(ec)
    )

  val route = Routes.create(jdbcClient).create
  val bindingFuture = Http().bindAndHandle(route, "0.0.0.0", 8080)

  sys.addShutdownHook {
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }

}
