package app.service.gateway.global.repository

import app.service.gateway.global.dto.Authority
import app.service.gateway.global.dto.JwtToken
import app.service.gateway.global.dto.User
import app.service.gateway.global.repository.base.ReactiveBaseRepository
import org.bson.types.ObjectId
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface UserRepository: ReactiveBaseRepository<User, ObjectId> {
    fun findUserByAccountOrEmail(account: String, email: String): Mono<User>
}

@Repository
interface BlacklistRepository: ReactiveBaseRepository<JwtToken, ObjectId> {
    fun findJwtTokensByToken(token: String): Mono<JwtToken>
}

@Repository
interface AuthorityRepository: ReactiveBaseRepository<Authority, ObjectId> {
    fun findAuthorityByAuthorityName(authorityName: String): Mono<Authority>
}