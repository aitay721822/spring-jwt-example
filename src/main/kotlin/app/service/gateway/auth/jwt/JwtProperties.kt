package app.service.gateway.auth.jwt

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "jwt.security")
data class JwtProperties (
        val prefix: String,
        val issuer: String,
        val secret: String
)