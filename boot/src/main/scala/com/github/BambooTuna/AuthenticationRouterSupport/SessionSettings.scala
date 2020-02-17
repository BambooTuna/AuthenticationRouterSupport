package com.github.BambooTuna.AuthenticationRouterSupport

import akka.http.scaladsl.server.Directive0
import com.github.BambooTuna.AuthenticationRouterSupport.model.LoginSession
import com.softwaremill.session.SessionDirectives.{
  invalidateSession,
  requiredSession,
  setSession
}
import com.softwaremill.session.SessionOptions.{
  oneOff,
  refreshable,
  usingHeaders
}
import com.softwaremill.session.{
  InMemoryRefreshTokenStorage,
  RefreshTokenStorage,
  SessionConfig,
  SessionManager,
  SessionSerializer,
  SingleValueSessionSerializer
}

import scala.concurrent.ExecutionContext
import io.circe._
import io.circe.generic.auto._
import io.circe.syntax._

class SessionSettings[S](config: SessionConfig = SessionConfig.fromConfig())(
    implicit
    serializer: SessionSerializer[S, String],
    refreshTokenStorage: RefreshTokenStorage[S],
    ec: ExecutionContext) {

  implicit val sessionManager = new SessionManager[S](config)

  def setSessionToken(v: S): Directive0 =
    setSession(refreshable, usingHeaders, v)
  val checkSessionToken = requiredSession(refreshable, usingHeaders)
  val invalidateSessionToken = invalidateSession(refreshable, usingHeaders)

}

object SessionSettings {
  def default(implicit e: ExecutionContext): SessionSettings[LoginSession] = {
    implicit val serializer: SessionSerializer[LoginSession, String] =
      new SingleValueSessionSerializer(
        _.asJson.noSpaces,
        (in: String) => parser.decode[LoginSession](in).toTry
      )
    implicit val refreshTokenStorage =
      new InMemoryRefreshTokenStorage[LoginSession] {
        override def log(msg: String) = println(s"refreshTokenStorage: $msg")
      }
    new SessionSettings[LoginSession]()
  }
}
