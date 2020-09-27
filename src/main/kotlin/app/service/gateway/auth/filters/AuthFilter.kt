package app.service.gateway.auth.filters

import app.service.gateway.ToRead
import app.service.gateway.auth.dto.FailureResponse
import app.service.gateway.auth.dto.LoginRequest
import app.service.gateway.auth.dto.LoginResponse
import app.service.gateway.auth.error.JsonParseError
import app.service.gateway.auth.error.RequestBodyEmptyError
import app.service.gateway.auth.jwt.JwtService
import app.service.gateway.auth.manager.UsernamePasswordAuthManager
import app.service.gateway.writeJson
import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.JsonMappingException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManagerResolver
import org.springframework.security.authentication.ReactiveAuthenticationManagerResolver
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import java.util.*
import javax.annotation.PostConstruct

@Component
class AuthFilter (
        resolver: ReactiveAuthenticationManagerResolver<ServerWebExchange>,
): AuthenticationWebFilter(resolver) {

    @Autowired
    private lateinit var messageSource: MessageSource

    @Autowired
    private lateinit var jwtService: JwtService

    @PostConstruct
    fun init(){

        setRequiresAuthenticationMatcher {
            ServerWebExchangeMatchers.pathMatchers("/auth/login").matches(it)
        }

        setServerAuthenticationConverter { exchange ->
            exchange.request
                    .body
                    .next()
                    .switchIfEmpty {
                        val message = messageSource.getMessage("request.bodyEmpty", null, Locale.ENGLISH)
                        Mono.error(RequestBodyEmptyError(message))
                    }
                    .flatMap { it.ToRead<LoginRequest>() }
                    .onErrorMap (JsonParseException::class.java) {
                        JsonParseError(messageSource.getMessage("request.json.parsing_error", null, Locale.ENGLISH))
                    }
                    .onErrorMap (JsonMappingException::class.java) {
                        JsonParseError(messageSource.getMessage("request.json.mapping_error", null, Locale.ENGLISH))
                    }
                    .onErrorMap (IllegalArgumentException::class.java) {
                        JsonParseError(messageSource.getMessage("request.json.format_incorrect", null, Locale.ENGLISH))
                    }
                    .filter { it.usernameOrEmailAddress != null && it.password != null }
                    .switchIfEmpty {
                        val e = JsonParseError(messageSource.getMessage("request.json.parsing_error", null, Locale.ENGLISH))
                        Mono.error(e)
                    }
                    .map { UsernamePasswordAuthenticationToken(it.usernameOrEmailAddress, it.password) }
        }

        setAuthenticationSuccessHandler { filters, authentication ->
            filters.exchange.response.writeJson(HttpStatus.OK, LoginResponse(
                    token = jwtService.encrypt(authentication.name, authentication.credentials as String, authentication.authorities)
            ))
        }

        setAuthenticationFailureHandler { filters, exception ->
            val response = FailureResponse(exception.message).apply { status = false }
            filters.exchange.response.writeJson(HttpStatus.UNAUTHORIZED, response)
        }

    }

}