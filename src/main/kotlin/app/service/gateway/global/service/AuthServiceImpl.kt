package app.service.gateway.global.service

import app.service.gateway.getLogger
import app.service.gateway.global.dto.Authority
import app.service.gateway.global.repository.AuthorityRepository
import app.service.gateway.global.service.base.BaseCrudService
import app.service.gateway.global.service.base.BaseCrudServiceImpl
import org.reactivestreams.Publisher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.kotlin.adapter.rxjava.toMaybe
import reactor.kotlin.core.publisher.switchIfEmpty
import java.util.*
import javax.annotation.PostConstruct

@Service
class AuthServiceImpl(@Autowired private val repository: AuthorityRepository) : BaseCrudServiceImpl<Authority>(repository), AuthService{

    companion object {
        private val log = getLogger()
    }

    /**
     * 提供其他服務使用唯獨的方式存取權限
     */
    val authority: List<Authority>
        get() = _authority.toList()
    // simple thread safe way
    private val _authority: MutableList<Authority> = Collections.synchronizedList(mutableListOf())

    private val scheduler = Schedulers.boundedElastic()

    private val addInitAuth = repository.findAuthorityByAuthorityName(AuthService.UserAuthority)
            .switchIfEmpty { repository.save(Authority(authorityName = AuthService.UserAuthority)) }
            .then(repository.findAuthorityByAuthorityName(AuthService.AdminAuthority))
            .switchIfEmpty { repository.save(Authority(authorityName = AuthService.AdminAuthority)) }

    @PostConstruct
    private fun init(){
        addInitAuth.thenMany(repository.findAll())
                .publishOn(scheduler)
                .doOnSubscribe { log.info("fetch authority...") }
                .doOnNext { log.info("authority ${it.authorityName} loaded") }
                .doOnComplete { log.info("authority fetch complete") }
                .subscribe { _authority.add(it) }
    }

    override fun findAuthorityByName(authorityName: String): Mono<Authority> =
            Mono.justOrEmpty(authority.find { it.authorityName == authorityName })
                    .switchIfEmpty {
                        repository.findAuthorityByAuthorityName(authorityName)
                                .doOnSuccess {
                                    if (!_authority.contains(it))
                                        _authority.add(it)
                                }
                    }

}