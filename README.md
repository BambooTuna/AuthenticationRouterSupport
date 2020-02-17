## Deploy
```bash
$ sbt publish
```

## test
1. build jar file
```bash
$ sbt docker:publishLocal
```

2. run
```bash
$ docekr-compoes up --build
```

### Sign Up
```bash
$ curl -X POST -H "Content-Type: application/json" -d '{"id":"bambootuna@gmail.com","pass":"pass"}' localhost:8080/signup
```

### Sign In (Generate Token)
Session Token will be set in header(`Set-Authorization`).
```bash
$ curl -X POST -H "Content-Type: application/json" -d '{"id":"bambootuna@gmail.com","pass":"pass"}' localhost:8080/signin -i
Set-Authorization: ~~~
```

### Check Session Token
```bash
$ curl -X GET -H "Authorization: ~~~" localhost:8080/health
```

### Log Out (Delete Session Token)
```bash
$ curl -X DELETE -H "Authorization: ~~~" localhost:8080/logout
```

## reference.conf
Do not change file name!
```conf
akka.http.session {

  server-secret = "c05ll3lesrinf39t7mc5h6un6r0c69lgfno69dsak3vabeqamouq4328cuaekros401ajdpkh60rrtpd8ro24rbuqmgtnd1ebag6ljnb65i8a55d482ok7o0nch0bfbe"

  cookie {
    name = "_sessiondata"
    domain = none
    path = /
    secure = false
    http-only = true
  }
  header {
    send-to-client-name = "Set-Authorization"
    get-from-client-name = "Authorization"
  }
  max-age = 30d
  encrypt-data = true

  jws {
    alg = "HS256"
  }

  jwt {
    iss = "Issuer"
    sub = "Subject"
    aud = "Audience"
    exp-timeout = 7 days
    nbf-offset = 5 minutes
    include-iat = true
    include-jti = true
  }

  csrf {
    cookie {
      name = "XSRF-TOKEN"
      domain = none
      path = /
      secure = false
      http-only = false
    }
    submitted-name = "X-XSRF-TOKEN"
  }

  refresh-token {
    cookie {
      name = "_refreshtoken"
      domain = none
      path = /
      secure = false
      http-only = true
    }
    header {
      send-to-client-name = "Set-Refresh-Token"
      get-from-client-name = "Refresh-Token"
    }
    max-age = 30 days
    remove-used-token-after = 5 seconds
  }

  token-migration {
    v0-5-2 {
      enabled = false
    }
    v0-5-3 {
      enabled = false
    }
  }
}

```

## Usage
1. Dependencies
```
resolvers += "Maven Repo on github" at "https://BambooTuna.github.com/AuthenticationRouterSupport"
"com.github.BambooTuna" %% "authenticationroutersupport" % "1.0.0-SNAPSHOT"
```
