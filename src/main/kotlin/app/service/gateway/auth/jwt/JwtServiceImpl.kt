package app.service.gateway.auth.jwt

import app.service.gateway.auth.dto.LoginRequest
import app.service.gateway.auth.error.JsonNotValidate
import app.service.gateway.global.MessageSourceAccessor
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.http.HttpHeaders
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.security.core.GrantedAuthority
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono
import java.util.*
import java.util.function.Predicate

@Service
class JwtServiceImpl(
        @Autowired private val properties: JwtProperties
): JwtService {


    @Autowired
    private lateinit var messageSource: MessageSourceAccessor

    private val matchJwt = Predicate<String> {
        val token = it.split(JwtService.sep)
        token.count() == 2 && token.first() == properties.prefix
    }

    override fun encrypt(subject: String, credential: String, authorities: Collection<GrantedAuthority>): String {
        val token = JWT.create()
                .withSubject(subject)
                .withIssuer(properties.issuer)
                .withClaim(JwtService.credentialKey, credential)
                .withArrayClaim(JwtService.rolesKey, authorities.map { it.authority }.toTypedArray())
                .withExpiresAt(Date.from(expireTime()))
                .sign(Algorithm.HMAC256(properties.secret))
        return properties.prefix + JwtService.sep + token
    }


    override fun decrypt(jwt: String): Mono<DecodedJWT> = Mono.justOrEmpty(
        JWT.require(Algorithm.HMAC256(properties.secret))
                .withIssuer(properties.issuer)
                .build()
                .verify(jwt)
    )

    override fun extract(request: ServerHttpRequest): Mono<String> = Mono.justOrEmpty(
            request.headers.getFirst(HttpHeaders.AUTHORIZATION)
    )

    override fun parse(jwt: String): Mono<String> =
            Mono.just(jwt)
                    .filter(matchJwt)
                    .switchIfEmpty {
                        val msg = messageSource.getMessage("request.json.format_incorrect")
                        Mono.error(JsonNotValidate(msg = msg))
                    }
                    .flatMap { it.split(JwtService.sep).last().toMono() }

}