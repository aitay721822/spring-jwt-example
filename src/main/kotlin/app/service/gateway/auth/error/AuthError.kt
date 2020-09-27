package app.service.gateway.auth.error

import org.springframework.security.core.AuthenticationException

class UsernameIsNull(msg: String): AuthenticationException(msg)

class UserNotFound(msg: String): AuthenticationException(msg)

class JsonParseError(msg: String): AuthenticationException(msg)

class RequestBodyEmptyError(msg: String): AuthenticationException(msg)

class JsonNotValidate(msg: String): AuthenticationException(msg)
