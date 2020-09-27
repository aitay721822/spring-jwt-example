package app.service.gateway.auth.config

import app.service.gateway.auth.filters.AuthFilter
import app.service.gateway.auth.filters.ValidFilter
import app.service.gateway.auth.manager.AuthResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManagerResolver
import org.springframework.security.authentication.ReactiveAuthenticationManagerResolver
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.invoke
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.AuthenticationFilter
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers
import org.springframework.web.server.ServerWebExchange

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class AuthConfig {

    @Autowired
    private lateinit var authFilter: AuthFilter

    @Autowired
    private lateinit var validFilter: ValidFilter

    @Autowired
    private lateinit var resolver: AuthResolver

    @Bean
    fun authRouteFilter(http: ServerHttpSecurity): SecurityWebFilterChain{
        return http {
            csrf { disable() }
            securityMatcher(PathPatternParserServerWebExchangeMatcher("/auth/**"))
            authorizeExchange {
                authorize("/auth/signup", permitAll)
                authorize("/auth/login", permitAll)
                authorize("/auth/logout", permitAll)
                authorize("/auth/me", authenticated)
                authorize(anyExchange, authenticated)
            }
            formLogin { disable() }
            httpBasic { disable() }
            addFilterAfter(AuthenticationWebFilter(resolver), SecurityWebFiltersOrder.REACTOR_CONTEXT)
            addFilterAt(authFilter, SecurityWebFiltersOrder.HTTP_BASIC)
            addFilterAt(validFilter, SecurityWebFiltersOrder.AUTHENTICATION)
        }
    }

}