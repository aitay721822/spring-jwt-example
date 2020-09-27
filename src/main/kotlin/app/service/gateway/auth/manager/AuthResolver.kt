package app.service.gateway.auth.manager

import app.service.gateway.global.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.ReactiveAuthenticationManagerResolver
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers
import org.springframework.stereotype.Component
import org.springframework.util.PathMatcher
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class AuthResolver(private val service: UserService): ReactiveAuthenticationManagerResolver<ServerWebExchange> {

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    override fun resolve(context: ServerWebExchange?): Mono<ReactiveAuthenticationManager> =
        Mono.justOrEmpty(context)
                .flatMap { ServerWebExchangeMatchers.pathMatchers(HttpMethod.POST, "/auth/login").matches(it) }
                .map {
                    if(it.isMatch) return@map UsernamePasswordAuthManager(services = service, encoder = passwordEncoder)
                    return@map JwtAuthManager(services = service)
                }
}