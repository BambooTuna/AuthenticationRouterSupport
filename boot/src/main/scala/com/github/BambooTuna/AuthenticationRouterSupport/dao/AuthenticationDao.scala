package com.github.BambooTuna.AuthenticationRouterSupport.dao

import cats.data.{Kleisli, OptionT}
import com.github.BambooTuna.AuthenticationRouterSupport.dao.DaoSupport.{F, I}
import com.github.BambooTuna.AuthenticationRouterSupport.model.AuthenticationData

class AuthenticationDao extends DoobieSupport {

  import dc._
  import doobie.implicits._

  implicit lazy val daoSchemaMeta =
    schemaMeta[AuthenticationData](
      "authentication_data",
      _.id -> "id",
      _.loginId -> "login_id",
      _.encryptionPass -> "encryption_pass"
    )

  def insert(record: AuthenticationData): F[AuthenticationData] =
    Kleisli { implicit ctx: I =>
      val q = quote {
        query[AuthenticationData]
          .insert(lift(record))
      }
      ctx.use(x => run(q).transact(x)).map(_ => record)
    }

  def resolveById(id: String): OptionT[F, AuthenticationData] =
    OptionT[F, AuthenticationData](
      Kleisli { implicit ctx: I =>
        val q = quote {
          query[AuthenticationData].filter(_.id == lift(id))
        }
        ctx.use(x => run(q).transact(x)).map(_.headOption)
      }
    )

  def resolveByLoginId(loginId: String): OptionT[F, AuthenticationData] =
    OptionT[F, AuthenticationData](
      Kleisli { implicit ctx: I =>
        val q = quote {
          query[AuthenticationData].filter(_.loginId == lift(loginId))
        }
        ctx.use(x => run(q).transact(x)).map(_.headOption)
      }
    )

}
