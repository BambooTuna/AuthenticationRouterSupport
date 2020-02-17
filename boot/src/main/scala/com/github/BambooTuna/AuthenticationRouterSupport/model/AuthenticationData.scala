package com.github.BambooTuna.AuthenticationRouterSupport.model

import java.util.UUID

case class AuthenticationData(id: String,
                              loginId: String,
                              encryptionPass: String) {

  def generateLoginSession: LoginSession = {
    val uuid = UUID.randomUUID().toString
    val token = new String(uuid.getBytes)
    LoginSession(id, token)
  }

}

object AuthenticationData {

  def generate(json: SignUpRequestJson): AuthenticationData =
    AuthenticationData(
      id = java.util.UUID.randomUUID.toString.replaceAll("-", ""),
      loginId = json.id,
      encryptionPass = json.encryption
    )

}
