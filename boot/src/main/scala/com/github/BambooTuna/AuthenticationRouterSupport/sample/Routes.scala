package com.github.BambooTuna.AuthenticationRouterSupport.sample

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpMethods._
import akka.stream.ActorMaterializer
import cats.effect.Resource
import com.github.BambooTuna.AuthenticationRouterSupport.dao.AuthenticationDao
import com.github.BambooTuna.AuthenticationRouterSupport.{
  AuthenticationRouter,
  Router,
  route
}
import doobie.hikari.HikariTransactor
import monix.eval.Task

object Routes {

  def create(jdbcClient: Resource[Task, HikariTransactor[Task]])(
      implicit system: ActorSystem,
      materializer: ActorMaterializer): Router = {
    val authenticationDao = new AuthenticationDao()
    val authenticationRouter = new AuthenticationRouter(authenticationDao)

    Router(
      route(POST, "signup", authenticationRouter.signUp(jdbcClient)),
      route(POST, "signin", authenticationRouter.generateToken(jdbcClient)),
      route(GET, "health", authenticationRouter.healthCheck),
      route(DELETE, "logout", authenticationRouter.logout)
    )
  }

}
