package app.service.gateway.global.service


import app.service.gateway.global.dto.JwtToken
import app.service.gateway.global.dto.User
import app.service.gateway.global.service.base.BaseCrudService
import com.auth0.jwt.interfaces.DecodedJWT
import reactor.core.publisher.Mono

interface BlacklistService: BaseCrudService<JwtToken> {

    /**
     * 將JWT黑名單
     *
     * @param token JWT權杖
     * @return 被儲存的Jwt
     */
    fun invalidate(token: DecodedJWT): Mono<JwtToken>

}