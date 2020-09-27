package app.service.gateway.auth.dto

import java.io.Serializable

data class LoginRequest (
        val usernameOrEmailAddress: String? = null,
        val password: String? = null
) : Serializable

data class SignupRequest (
        val username: String,
        val email: String,
        val password: String
) : Serializable