package app.service.gateway.global.service

import app.service.gateway.global.dto.JwtToken
import app.service.gateway.global.dto.User
import app.service.gateway.global.repository.BlacklistRepository
import app.service.gateway.global.service.base.BaseCrudServiceImpl
import com.auth0.jwt.interfaces.DecodedJWT
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class BlacklistServiceImpl(
        @Autowired private val repository: BlacklistRepository
): BaseCrudServiceImpl<JwtToken>(repository), BlacklistService {

    override fun invalidate(token: DecodedJWT): Mono<JwtToken> = repository.save(JwtToken(token = token.token, invalidate = true))

}