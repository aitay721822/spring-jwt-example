package app.service.gateway.auth.error

class SignupValidateFailure(msg: String): Exception(msg)

class UserIsExisted(msg: String) : Exception(msg)
