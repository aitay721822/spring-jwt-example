package app.service.gateway.global

import app.service.gateway.getLogger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Component
import java.util.*

@Component
class MessageSourceAccessor(
        @Autowired private val messageSource: MessageSource
) {

    companion object {
        private val log = getLogger()
    }

    var defaultLocale = LocaleContextHolder.getLocale()
        set(value) {
            log.info("System language Changed to ${value.country}")
            field = value
        }

    fun getMessage(code: String, parameter: Array<Any>? = null, fallback: String = ""): String =
            messageSource.getMessage(code, parameter, fallback, defaultLocale) ?: fallback
}