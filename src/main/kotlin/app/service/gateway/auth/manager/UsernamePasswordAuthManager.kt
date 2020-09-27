package app.service.gateway.auth.manager

import app.service.gateway.global.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.*
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import javax.annotation.PostConstruct

class UsernamePasswordAuthManager(
        private val services: UserService,
        encoder: PasswordEncoder
) : UserDetailsRepositoryReactiveAuthenticationManager(services) {

    init {
        setPasswordEncoder(encoder)
    }

    override fun retrieveUser(username: String?): Mono<UserDetails> =
            services.findByUsername(username)
}