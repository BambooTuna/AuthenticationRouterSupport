package com.github.BambooTuna.AuthenticationRouterSupport

import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.headers._
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Directive0, Route}
//import com.typesafe.config.ConfigFactory

trait CorsSupport {
//  lazy val allowedOrigin = {
//    val config         = ConfigFactory.load()
//    val sAllowedOrigin = config.getString("boot.allowed-origin")
//    HttpOrigin(sAllowedOrigin)
//  }

  //this directive adds access control headers to normal responses
  private def addAccessControlHeaders: Directive0 = {
    respondWithHeaders(
      `Access-Control-Allow-Origin`.`*`,
      `Access-Control-Allow-Credentials`(true),
      `Access-Control-Allow-Headers`("Authorization",
                                     "Content-Type",
                                     "X-Requested-With"),
      `Access-Control-Expose-Headers`("Set-Authorization", "Set-Refresh-Token")
    )
  }

  //this handles preflight OPTIONS requests.
  private def preflightRequestHandler: Route = options {
    complete(
      HttpResponse(StatusCodes.OK).withHeaders(
        `Access-Control-Allow-Methods`(OPTIONS, POST, PUT, GET, DELETE)))
  }

  def corsHandler(r: Route) = addAccessControlHeaders {
    preflightRequestHandler ~ r
  }
}
