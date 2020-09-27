package app.service.gateway.global.config

import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.convert.MappingMongoConverter
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories

@Configuration
class MongoConfig : AbstractReactiveMongoConfiguration() {

    override fun getDatabaseName(): String  = "Db"

    override fun autoIndexCreation(): Boolean = true

    override fun reactiveMongoClient(): MongoClient = MongoClients.create()

}