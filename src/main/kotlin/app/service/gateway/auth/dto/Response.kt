package app.service.gateway.auth.dto

import app.service.gateway.auth.dto.base.BaseResponse

data class LoginResponse(
        val token: String,
) : BaseResponse()

data class LogoutResponse(
        val message: String
) : BaseResponse()

data class SignupResponse(
        val username: String,
        val email: String
) : BaseResponse()

data class FailureResponse(
        val error: String?
) : BaseResponse()