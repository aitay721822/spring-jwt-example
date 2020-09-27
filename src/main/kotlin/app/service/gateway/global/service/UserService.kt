package app.service.gateway.global.service

import app.service.gateway.auth.dto.*
import app.service.gateway.global.dto.Authority
import app.service.gateway.global.dto.JwtToken
import app.service.gateway.global.dto.User
import app.service.gateway.global.service.base.BaseCrudService
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetailsService
import reactor.core.publisher.Mono

interface UserService: ReactiveUserDetailsService, BaseCrudService<User> {

    /**
     * 註冊功能
     */
    fun signup(entity: SignupRequest): Mono<SignupResponse>

    /**
     * 將使用者登出並將JWT令牌標註為無效的
     * 流程為：
     *  1. 登出請求送達伺服器
     *  2. 檢查伺服器是否有此令牌，若無，則走第四步
     *  3. 將此令牌標註為無效
     *  4. 回傳Response
     */
    fun logout(token: String?): Mono<LogoutResponse>

    /**
     * 尋找資料庫中使用者的資料
     *
     * @param username 帳號或是電子郵件
     * @return User instance
     */
    fun findUserByAccountOrEmail(username: String, email: String): Mono<User>

}