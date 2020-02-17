package com.github.BambooTuna.AuthenticationRouterSupport.dao

import java.time.LocalDateTime

import doobie.quill.DoobieContext
import io.getquill.SnakeCase

class DoobieSupport {

  val dc: DoobieContext.MySQL[SnakeCase] = new DoobieContext.MySQL(SnakeCase)
  import dc._

  implicit class LocalDateTimeQuotes(left: LocalDateTime) {
    def >(right: LocalDateTime) = quote(infix"$left > $right".as[Boolean])
    def >=(right: LocalDateTime) = quote(infix"$left >= $right".as[Boolean])

    def <(right: LocalDateTime) = quote(infix"$left < $right".as[Boolean])
    def <=(right: LocalDateTime) = quote(infix"$left <= $right".as[Boolean])
  }

  val cos = quote { (i: Double) =>
    infix"cos($i)".as[Double]
  }

  val sin = quote { (i: Double) =>
    infix"sin($i)".as[Double]
  }

  val acos = quote { (i: Double) =>
    infix"acos($i)".as[Double]
  }

  val radians = quote { (i: Double) =>
    infix"radians($i)".as[Double]
  }

}
