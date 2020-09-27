package app.service.gateway.global.repository.base

import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.data.repository.NoRepositoryBean
import reactor.core.publisher.Flux

@NoRepositoryBean
interface ReactiveBaseRepository<T, ID> : ReactiveMongoRepository<T, ID> {
    fun findByIdNotNullOrderByIdAsc(pageable: Pageable): Flux<T>
}
