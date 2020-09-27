package app.service.gateway.auth.dto.base

import app.service.gateway.format
import java.io.Serializable
import java.util.*

open class BaseResponse (
        var status: Boolean = true,
        val timestamp: String = Date().format()
) : Serializable