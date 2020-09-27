package app.service.gateway.auth.filters

import app.service.gateway.auth.jwt.JwtAuthenticationToken
import app.service.gateway.auth.jwt.JwtService
import app.service.gateway.auth.manager.JwtAuthManager
import app.service.gateway.auth.manager.UsernamePasswordAuthManager
import app.service.gateway.global.MessageSourceAccessor
import app.service.gateway.global.service.BlacklistService
import app.service.gateway.global.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.security.authentication.AuthenticationManagerResolver
import org.springframework.security.authentication.ReactiveAuthenticationManagerResolver
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono
import java.util.*
import javax.annotation.PostConstruct

@Component
class ValidFilter (
        resolver: ReactiveAuthenticationManagerResolver<ServerWebExchange>
): AuthenticationWebFilter(resolver) {

    @Autowired
    private lateinit var jwtService: JwtService

    @PostConstruct
    fun init(){
        setServerAuthenticationConverter { exchange ->
            Mono.just(exchange.request)
                    .flatMap { jwtService.extract(it) }
                    .flatMap { jwtService.parse(it) }
                    .flatMap { jwtService.decrypt(it) }
                    .flatMap { JwtAuthenticationToken.instance(it.toMono()) }
        }
    }
}
