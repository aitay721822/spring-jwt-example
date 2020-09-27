package app.service.gateway.global.service

import app.service.gateway.auth.dto.LogoutResponse
import app.service.gateway.auth.dto.SignupRequest
import app.service.gateway.auth.dto.SignupResponse
import app.service.gateway.auth.error.SignupValidateFailure
import app.service.gateway.auth.error.UserIsExisted
import app.service.gateway.auth.error.UserNotFound
import app.service.gateway.auth.error.UsernameIsNull
import app.service.gateway.auth.jwt.JwtService
import app.service.gateway.global.MessageSourceAccessor
import app.service.gateway.global.dto.User
import app.service.gateway.global.error.ResourceNotFound
import app.service.gateway.global.repository.UserRepository
import app.service.gateway.global.service.base.BaseCrudServiceImpl
import com.auth0.jwt.interfaces.DecodedJWT
import com.mongodb.DuplicateKeyException
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono
import java.util.function.Predicate

@Service
class UserServiceImpl(
        private val userRepository: UserRepository,
) : BaseCrudServiceImpl<User>(userRepository), UserService {

    @Autowired
    private lateinit var messageSource: MessageSourceAccessor

    @Autowired
    private lateinit var authService: AuthService

    @Autowired
    private lateinit var jwtService: JwtService

    @Autowired
    private lateinit var blacklistService: BlacklistService

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    private val preCheck = Predicate<SignupRequest> {
        return@Predicate true
    }

    override fun signup(entity: SignupRequest): Mono<SignupResponse> =
            Mono.just(entity)
                    .filter(preCheck)
                    .switchIfEmpty {
                        val msg = messageSource.getMessage("user.signup.inputError")
                        Mono.error(SignupValidateFailure(msg = msg))
                    }
                    .flatMap { SignupRequest(username = it.username, password = passwordEncoder.encode(it.password), email = it.email).toMono() }
                    .zipWith(authService.findAuthorityByName(AuthService.UserAuthority)) { request, authority ->
                        val authId = authority.id
                        if (authId == null){
                            val msg = messageSource.getMessage("server.internal.resourcesNotFound")
                            throw ResourceNotFound(msg)
                        }
                        else {
                            User(
                                username = request.username,
                                password = request.password,
                                email = request.email,
                                isLocked = false,
                                authorityIds = listOf(authId),
                                blockedListIds = listOf()
                            )
                        }
                    }
                    .flatMap { userRepository.save(it) }
                    .flatMap { SignupResponse(username = it.username, email = it.email).toMono() }
                    .onErrorMap (DuplicateKeyException::class.java) {
                        val msg = messageSource.getMessage("user.signup.isExisted")
                        UserIsExisted(msg = msg)
                    }

    private fun invalidate(username: String, token: DecodedJWT): Mono<Boolean> =
            Mono.zip(userRepository.findUserByAccountOrEmail(username.toLowerCase(), username), blacklistService.invalidate(token)) { user, jwt ->
                        val newBlockedList: List<ObjectId> = user.blockedListIds.toMutableList().apply { add(jwt.id!!) }
                        return@zip user.copy(blockedListIds = newBlockedList)
                    }
                    .flatMap { userRepository.save(it) }
                    .map { true }
                    .onErrorReturn(false)

    override fun logout(token: String?): Mono<LogoutResponse> =
            Mono.justOrEmpty(token)
                    .flatMap { jwtService.parse(it) }
                    .flatMap { jwtService.decrypt(it) }
                    .flatMap { invalidate(it.subject, it) }
                    .flatMap {
                        val msg = messageSource.getMessage("user.logout.messages")
                        Mono.just(LogoutResponse(message = msg).apply { status = it })
                    }
                    .switchIfEmpty {
                        val msg = messageSource.getMessage("user.logout.messages")
                        Mono.just(LogoutResponse(message = msg))
                    }


    private fun loadInfo(user: User): Mono<User> {
        val authIds = authService.findAllById(user.authorityIds).collectList()
        val blockIds = blacklistService.findAllById(user.blockedListIds).collectList()
        return Mono.zip(authIds, blockIds) { authority, blockList ->
            user.copy(authority = authority, blockedList = blockList)
        }
    }

    override fun findUserByAccountOrEmail(username: String, email: String): Mono<User> {
        return userRepository.findUserByAccountOrEmail(username, email)
                .flatMap { loadInfo(it) }
    }

    override fun findByUsername(username: String?): Mono<UserDetails> {
        if (username == null)
            return Mono.error(
                UsernameIsNull(msg = messageSource.getMessage("user.usernameEmpty"))
            )
        return findUserByAccountOrEmail(username.toLowerCase(), username)
                .map { it.toUserDetails() }
                .switchIfEmpty {
                    val msg = messageSource.getMessage("user.userNotFound")
                    Mono.error(UserNotFound(msg = msg))
                }
    }

}