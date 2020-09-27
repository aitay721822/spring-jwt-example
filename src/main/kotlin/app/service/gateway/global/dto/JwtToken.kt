package app.service.gateway.global.dto

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.io.Serializable

@Document(collection = "token_list")
data class JwtToken (
        @Id var id: ObjectId? = null,
        @Indexed(name = "token", unique = true) var token: String,
        @Indexed(name = "invalidate") var invalidate: Boolean,
        @Version val version: Long? = null
) : Serializable