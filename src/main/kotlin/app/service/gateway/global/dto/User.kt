package app.service.gateway.global.dto

import org.bson.types.ObjectId
import org.springframework.data.annotation.*
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.MongoId
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.validation.annotation.Validated
import java.io.Serializable
import java.util.*

@Document("user")
data class User(
        @Id var id: ObjectId? = null,
        @Indexed(name = "username", unique = true) var username: String,
        @Indexed(name = "account", unique = true) var account: String = username.toLowerCase(),
        @Indexed(name = "password") var password: String,
        @Indexed(name = "email", unique = true) var email: String,
        @Indexed(name = "islocked") var isLocked: Boolean = false,
        @CreatedDate @Indexed(name = "createdDate") var createdDate: Date = Date(),
        val authorityIds: List<ObjectId>,
        val blockedListIds: List<ObjectId>,
        @Transient val authority: List<Authority> = listOf(),
        @Transient val blockedList: List<JwtToken> = listOf(),
        @Version val version: Long? = null
) : Serializable {

    // let spring data to know how to construct a User Object from Mongo returned data
    @PersistenceConstructor
    constructor(
            id: ObjectId,
            username: String,
            account: String,
            password: String,
            email: String,
            isLocked: Boolean,
            createdDate: Date,
            authorityIds: List<ObjectId>,
            blockedListIds: List<ObjectId>,
            version: Long?
    ): this(id = id, username = username, account = account, password = password, email = email, isLocked = isLocked, createdDate = createdDate, authorityIds = authorityIds, blockedListIds = blockedListIds, authority = listOf(), blockedList = listOf(), version = version)

    fun toUserDetails(): UserDetails {
        return User.builder()
                .username(username)
                .password(password)
                .accountLocked(isLocked)
                .authorities(authority.map { SimpleGrantedAuthority(it.authorityName) })
                .build()
    }
}