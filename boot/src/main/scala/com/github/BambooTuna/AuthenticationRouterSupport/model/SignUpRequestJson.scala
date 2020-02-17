package com.github.BambooTuna.AuthenticationRouterSupport.model

import java.math.BigInteger
import java.security.MessageDigest

case class SignUpRequestJson(id: String, pass: String) {

  def encryption: String = sha256(pass)
  private val SHA256 = MessageDigest.getInstance("SHA-256")
  private def sha256(input: String): String =
    String.format("%064x",
                  new BigInteger(1, SHA256.digest(input.getBytes("UTF-8"))))

}
