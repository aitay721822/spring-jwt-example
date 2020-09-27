package app.service.gateway

import app.service.gateway.auth.dto.LoginRequest
import app.service.gateway.auth.dto.LoginResponse
import app.service.gateway.auth.dto.SignupRequest
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.returnResult

@SpringBootTest
@AutoConfigureWebTestClient
class GatewayApplicationTests {

	@Autowired
	private lateinit var client: WebTestClient

	@Test
	fun contextLoads() { }

	@Test
	@Order(1)
	fun signup(){
		val request = SignupRequest(username = "testUser", email = "testEmail", password = "testPassword")
		client.post()
				.uri("/auth/signup")
				.bodyValue(request)
				.exchange()
				.expectStatus()
				.is2xxSuccessful
	}

	@Test
	@Order(2)
	fun login(){
		val request = LoginRequest(usernameOrEmailAddress = "testUser", password = "testPassword")
		client.post()
				.uri("/auth/login")
				.bodyValue(request)
				.exchange()
				.expectStatus()
				.is2xxSuccessful
	}

	@Test
	@Order(3)
	fun me(){
		val request = LoginRequest(usernameOrEmailAddress = "testUser", password = "testPassword")
		val response = client.post()
				.uri("/auth/login")
				.bodyValue(request)
				.exchange()
				.expectStatus()
				.is2xxSuccessful
				.returnResult<LoginResponse>()
				.responseBody
				.next()
				.block()
		if (response != null){
			client.get()
					.uri("/auth/me")
					.header(HttpHeaders.AUTHORIZATION, response.token)
					.exchange()
					.expectStatus()
					.is2xxSuccessful
		}
	}

	@Test
	@Order(4)
	fun logout(){
		val request = LoginRequest(usernameOrEmailAddress = "testUser", password = "testPassword")
		val response = client.post()
				.uri("/auth/login")
				.bodyValue(request)
				.exchange()
				.expectStatus()
				.is2xxSuccessful
				.returnResult<LoginResponse>()
				.responseBody
				.next()
				.block()
		if (response != null){
			client.post()
					.uri("/auth/logout")
					.header(HttpHeaders.AUTHORIZATION, response.token)
					.exchange()
					.expectStatus()
					.is2xxSuccessful
			client.get()
					.uri("/auth/me")
					.header(HttpHeaders.AUTHORIZATION, response.token)
					.exchange()
					.expectStatus()
					.is4xxClientError
		}
	}
}
