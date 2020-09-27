package app.service.gateway.global.service

import app.service.gateway.global.dto.Authority
import app.service.gateway.global.service.base.BaseCrudService
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface AuthService: BaseCrudService<Authority> {

    companion object {
        const val UserAuthority = "Auth_User"
        const val AdminAuthority = "Auth_Admin"
    }

    /**
     * 尋找名子吻合的權限
     * @return Authority Object
     * @param authorityName 權限名
     */
    fun findAuthorityByName(authorityName: String): Mono<Authority>

}