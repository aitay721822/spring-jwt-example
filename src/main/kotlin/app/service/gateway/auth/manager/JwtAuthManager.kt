package app.service.gateway.auth.manager

import app.service.gateway.auth.jwt.JwtAuthenticationToken
import app.service.gateway.getLogger
import app.service.gateway.global.service.UserService
import org.springframework.security.authentication.*
import org.springframework.security.core.Authentication
import org.springframework.security.core.SpringSecurityMessageSource
import org.springframework.security.core.userdetails.UserDetailsChecker
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.kotlin.core.publisher.switchIfEmpty

class JwtAuthManager(
        private val services: UserService,
) : ReactiveAuthenticationManager {

    // ~ properties
    companion object {
        private val log = getLogger()
    }

    private val messageSource = SpringSecurityMessageSource.getAccessor()

    private val schedulers = Schedulers.boundedElastic()

    private val preAuthenticationCheck = UserDetailsChecker {
        if (!it.isEnabled){
            log.debug("User account is disabled!")
            throw DisabledException(messageSource.getMessage("AbstractUserDetailsAuthenticationProvider.locked"))
        }
        if (!it.isAccountNonExpired) {
            log.debug("User account is expired!")
            throw AccountExpiredException(messageSource.getMessage("AbstractUserDetailsAuthenticationProvider.expired"))
        }
        if (!it.isAccountNonLocked){
            log.debug("User account is locked!")
            throw CredentialsExpiredException(messageSource.getMessage("AbstractUserDetailsAuthenticationProvider.locked"))
        }
    }

    private val postAuthenticationCheck = UserDetailsChecker {
        if (!it.isCredentialsNonExpired) {
            log.debug("Credentials is expired!")
            throw CredentialsExpiredException(messageSource.getMessage("AbstractUserDetailsAuthenticationProvider.credentialsExpired"))
        }
    }

    // ~ method
    override fun authenticate(authentication: Authentication?): Mono<Authentication> {
        if (authentication == null) return Mono.empty()
        val username = authentication.name
        val credential = authentication.credentials
        return services.findUserByAccountOrEmail(username.toLowerCase(), username)
                .filter { !it.blockedList.any { element -> element.token == credential } }
                .switchIfEmpty { throw CredentialsExpiredException(messageSource.getMessage("AbstractUserDetailsAuthenticationProvider.credentialsExpired")) }
                .publishOn(schedulers)
                .map { it.toUserDetails() }
                .doOnNext { preAuthenticationCheck.check(it) }
                .doOnNext { postAuthenticationCheck.check(it) }
                .flatMap { JwtAuthenticationToken.instance(it) }
                .map { it.apply { it.isAuthenticated = true } }
    }

}