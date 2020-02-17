package com.github.BambooTuna.AuthenticationRouterSupport

import akka.actor.ActorSystem
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Directive, Route, RouteResult}
import akka.stream.ActorMaterializer
import cats.effect.Resource
import com.github.BambooTuna.AuthenticationRouterSupport.dao.AuthenticationDao
import com.github.BambooTuna.AuthenticationRouterSupport.model.{
  AuthenticationData,
  LoginSession,
  SignUpRequestJson
}
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import doobie.hikari.HikariTransactor
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global

import scala.concurrent.Future
import scala.util.{Failure, Success}

import io.circe.generic.auto._

class AuthenticationRouter(authenticationDao: AuthenticationDao)(
    implicit system: ActorSystem,
    materializer: ActorMaterializer)
    extends FailFastCirceSupport {
  protected val session = SessionSettings.default(system.dispatcher)

  type QueryP[Q] = Directive[Q] => Route

  def signUp(dbSession: Resource[Task, HikariTransactor[Task]]): QueryP[Unit] =
    _ {
      entity(as[SignUpRequestJson]) { json =>
        val authenticationData = AuthenticationData.generate(json)
        val f: Future[LoginSession] =
          authenticationDao
            .insert(authenticationData)
            .map(_.generateLoginSession)
            .run(dbSession)
            .runToFuture

        onComplete(f) {
          case Success(value) =>
            session.setSessionToken(value) { complete(StatusCodes.OK) }
          case Failure(_) => complete(StatusCodes.Forbidden)
        }
      }.andThen(_.recover {
        case _ =>
          RouteResult.Complete(HttpResponse(status = StatusCodes.BadRequest))
      })
    }

  def generateToken(
      dbSession: Resource[Task, HikariTransactor[Task]]): QueryP[Unit] = _ {
    entity(as[SignUpRequestJson]) { json =>
      val f: Future[Option[LoginSession]] =
        authenticationDao
          .resolveByLoginId(json.id)
          .filter(_.encryptionPass == json.encryption)
          .map(_.generateLoginSession)
          .value
          .run(dbSession)
          .runToFuture

      onComplete(f) {
        case Success(value) =>
          value.fold[Route](
            complete(StatusCodes.Forbidden)
          )(
            t => session.setSessionToken(t) { complete(StatusCodes.OK) }
          )
        case Failure(_) => complete(StatusCodes.Forbidden)
      }
    }.andThen(_.recover {
      case _ =>
        RouteResult.Complete(HttpResponse(status = StatusCodes.BadRequest))
    })
  }

  def healthCheck: QueryP[Unit] = _ {
    session.checkSessionToken { _ =>
      complete(StatusCodes.OK)
    }
  }

  def logout: QueryP[Unit] = _ {
    session.checkSessionToken { _ =>
      session.invalidateSessionToken {
        complete(StatusCodes.OK)
      }
    }
  }

}
