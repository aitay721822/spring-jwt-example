package app.service.gateway

import app.service.gateway.auth.jwt.JwtProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@SpringBootApplication
@EnableReactiveMongoRepositories
@ConfigurationPropertiesScan(basePackageClasses = [JwtProperties::class])
class GatewayApplication {
	@Bean
	fun passwordEncoder() = BCryptPasswordEncoder()
}

fun main(args: Array<String>) {
	runApplication<GatewayApplication>(*args)
}