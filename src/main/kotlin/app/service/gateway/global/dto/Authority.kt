package app.service.gateway.global.dto

import org.bson.types.ObjectId
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.io.Serializable
import java.util.*

@Document("authority")
data class Authority(
        @Id var id: ObjectId? = null,
        @Indexed(name = "authority",unique = true) val authorityName: String,
        @Indexed(name = "createdDate") @CreatedDate val createdDate: Date = Date(),
        @Version val version: Long? = null
) : Serializable