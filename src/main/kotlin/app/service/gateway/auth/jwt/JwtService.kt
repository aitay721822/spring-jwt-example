package app.service.gateway.auth.jwt

import com.auth0.jwt.interfaces.DecodedJWT
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.security.core.GrantedAuthority
import reactor.core.publisher.Mono
import java.time.Instant
import java.time.Period
import java.util.*

interface JwtService {

    companion object {
        const val rolesKey = "Authorities"
        const val credentialKey = "Credential"
        const val sep = " "
    }

    fun encrypt(subject: String, credential: String, authorities: Collection<GrantedAuthority>): String

    fun decrypt(jwt: String): Mono<DecodedJWT>

    fun extract(request: ServerHttpRequest): Mono<String>

    fun parse(jwt: String): Mono<String>

    fun expireTime(): Instant = Date().toInstant().plus(Period.ofDays(1))

}