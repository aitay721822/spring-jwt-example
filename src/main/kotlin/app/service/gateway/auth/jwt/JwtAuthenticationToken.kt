package app.service.gateway.auth.jwt

import com.auth0.jwt.interfaces.DecodedJWT
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import reactor.core.publisher.Mono
import java.util.*

class JwtAuthenticationToken(
        private val identifier: Any,
        private val passport: Any,
        authorities: Collection<GrantedAuthority>
): AbstractAuthenticationToken(authorities.toMutableList()) {

    // ~ properties
    private var credentialsErased = false
    private var checkedAuthentication = false

    // ~ method
    override fun isAuthenticated(): Boolean = checkedAuthentication
    override fun setAuthenticated(authenticated: Boolean) {
        checkedAuthentication = authenticated
    }
    override fun getCredentials(): Any? = if (credentialsErased) null else passport
    override fun getPrincipal(): Any = identifier
    override fun eraseCredentials() {
        super.eraseCredentials()
        credentialsErased = true
    }

    companion object {
        fun instance(jwt: Mono<DecodedJWT>): Mono<out Authentication> =
                jwt
                        .flatMap {
                            if (it.claims.containsKey(JwtService.credentialKey) && it.claims.containsKey(JwtService.rolesKey)) {
                                val credential = it.claims[JwtService.credentialKey]
                                val authorities = it.claims[JwtService.rolesKey]
                                if (credential != null && authorities != null){
                                    return@flatMap Mono.just(JwtAuthenticationToken(
                                            it.subject,
                                            it.token,
                                            authorities.asArray(String::class.java).map { SimpleGrantedAuthority(it) }
                                    ))
                                }
                            }
                            return@flatMap Mono.empty()
                        }

        fun instance(UserInstance: UserDetails): Mono<out Authentication> =
                Mono.just(UserInstance)
                        .map { JwtAuthenticationToken(it.username, it.password, it.authorities) }
    }

}