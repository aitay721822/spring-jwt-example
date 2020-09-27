package app.service.gateway

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.server.reactive.ServerHttpResponse
import reactor.core.publisher.Mono
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*

val mapper = ObjectMapper()

inline fun <reified T> T.getLogger(): Logger {
    return LoggerFactory.getLogger(T::class.java)
}

inline fun <reified T> DataBuffer.ToRead(): Mono<T> =
        Mono.just(this)
                .flatMap {
                    try{
                        val json = mapper.readValue<T>(it.asInputStream())
                        val conv = mapper.convertValue(json, T::class.java)
                        Mono.just(conv)
                    }
                    catch (e: JsonParseException){
                        Mono.error(e)
                    }
                    catch (e: JsonMappingException){
                        Mono.error(e)
                    }
                    catch (e: IllegalArgumentException) {
                        // maybe convert to LinkedHashMap because Jackson tool not have enough information to deserialization
                        Mono.error(e)
                    }
                }

inline fun <reified T> ServerHttpResponse.writeJson(statusCode: HttpStatus, content: T): Mono<Void>{
    this.statusCode = statusCode
    this.headers.apply {
        this.accept = listOf(MediaType.APPLICATION_JSON)
        this.contentType = MediaType.APPLICATION_JSON
    }
    return Mono.just(content)
            .map { mapper.writeValueAsBytes(content) }
            .map { bufferFactory().wrap(it) }
            .flatMap { writeWith(Mono.just(it)) }
}

fun Date.format(): String{
    val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
    return sdf.format(this)
}

fun Instant.format(): String = Date.from(this).format()