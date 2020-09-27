package app.service.gateway.auth

import app.service.gateway.auth.dto.LogoutResponse
import app.service.gateway.auth.dto.SignupRequest
import app.service.gateway.auth.dto.SignupResponse
import app.service.gateway.global.dto.JwtToken
import app.service.gateway.global.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@RestController
@RequestMapping("/auth")
class AuthController (@Autowired private val userService: UserService) {

    @GetMapping("/me")
    fun me(auth: Authentication): Mono<String> = Mono.just(auth.name)

    @PostMapping("/signup")
    fun signup(@RequestBody request: SignupRequest): Mono<ResponseEntity<SignupResponse>> =
            userService.signup(request)
                    .map { ResponseEntity.ok(it) }

    @RequestMapping("/logout")
    fun logout(@RequestHeader(HttpHeaders.AUTHORIZATION, required = false) header: String?): Mono<ResponseEntity<LogoutResponse>> =
            userService.logout(header)
                    .map { ResponseEntity.ok(it) }

}