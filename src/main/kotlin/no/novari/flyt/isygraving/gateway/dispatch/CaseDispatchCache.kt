package no.novari.flyt.isygraving.gateway.dispatch

import no.novari.flyt.isygraving.gateway.instance.model.CaseInstance
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

data class CaseDispatchContext(
    val tenant: String,
    val caseArchiveGuid: String,
    val callbackUrl: String,
)

@Service
class CaseDispatchCache {
    private val cache = ConcurrentHashMap<String, CaseDispatchContext>()

    fun put(caseInstance: CaseInstance) {
        cache[caseInstance.caseId] =
            CaseDispatchContext(
                tenant = caseInstance.tenant,
                caseArchiveGuid = caseInstance.caseArchiveGuid,
                callbackUrl = caseInstance.callback,
            )
    }

    fun get(caseId: String): CaseDispatchContext? = cache[caseId]

    fun remove(caseId: String) {
        cache.remove(caseId)
    }
}
