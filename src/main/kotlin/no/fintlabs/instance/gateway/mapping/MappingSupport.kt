package no.fintlabs.instance.gateway.mapping

import java.util.UUID
import java.util.function.Function
import no.fintlabs.gateway.webinstance.model.File as WebFile

internal typealias PersistFile = Function<WebFile, UUID>

internal operator fun <T, R> Function<T, R>.invoke(t: T): R = apply(t)

internal fun MutableMap<String, String>.putOrEmpty(key: String, value: Any?) {
    put(key, value?.toString() ?: "")
}
