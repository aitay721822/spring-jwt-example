package app.service.gateway.global.service.base

import app.service.gateway.global.repository.base.ReactiveBaseRepository
import org.bson.types.ObjectId
import org.springframework.data.domain.Example
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface BaseCrudService<T>{

    fun findById(id: ObjectId): Mono<T>

    fun findAll(): Flux<T>

    fun save(entity: T): Mono<T>

    fun remove(entity: T): Mono<Void>

    fun remove(entity: Example<T>): Mono<Void>

    fun findAllById(id: Iterable<ObjectId>): Flux<T>

    fun findAllPaged(pageable: Pageable): Flux<T>

    fun findAllSorted(sort: Sort): Flux<T>

}