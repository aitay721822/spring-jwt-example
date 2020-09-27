package app.service.gateway.global.service.base

import app.service.gateway.global.repository.base.ReactiveBaseRepository
import org.bson.types.ObjectId
import org.springframework.data.domain.Example
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

open class BaseCrudServiceImpl<T> (private val repository: ReactiveBaseRepository<T, ObjectId>): BaseCrudService<T>{

    override fun findAll(): Flux<T> = repository.findAll()

    override fun save(entity: T): Mono<T> = repository.save(entity)

    override fun remove(entity: T) = repository.delete(entity)

    override fun remove(entity: Example<T>) = repository
            .findOne(entity)
            .flatMap { repository.delete(it) }

    override fun findAllPaged(pageable: Pageable) = repository.findByIdNotNullOrderByIdAsc(pageable)

    override fun findAllSorted(sort: Sort) = repository.findAll(sort)

    override fun findById(id: ObjectId): Mono<T> = repository.findById(id)

    override fun findAllById(id: Iterable<ObjectId>): Flux<T> = repository.findAllById(id)

}