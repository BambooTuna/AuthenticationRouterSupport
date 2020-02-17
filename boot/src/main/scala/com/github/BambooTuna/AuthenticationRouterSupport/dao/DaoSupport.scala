package com.github.BambooTuna.AuthenticationRouterSupport.dao

import cats.data.Kleisli
import cats.effect.Resource
import doobie.hikari.HikariTransactor
import monix.eval.Task

object DaoSupport {

  type I = Resource[Task, HikariTransactor[Task]]
  type F[O] = Kleisli[Task, I, O]

}
