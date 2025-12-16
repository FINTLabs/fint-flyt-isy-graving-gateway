package no.novari.flyt.isygraving.gateway.instance

import jakarta.validation.Valid
import no.novari.flyt.gateway.webinstance.InstanceProcessor
import no.novari.flyt.isygraving.gateway.instance.model.CaseInstance
import no.novari.flyt.isygraving.gateway.instance.model.JournalPostInstance
import no.novari.flyt.webresourceserver.UrlPaths.EXTERNAL_API
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("$EXTERNAL_API/isygraving/instances")
class IsyGravingInstanceController(
    private val caseInstanceProcessor: InstanceProcessor<CaseInstance>,
    private val journalPostInstanceProcessor: InstanceProcessor<JournalPostInstance>,
) {
    @PostMapping("case")
    fun postCase(
        @Valid @RequestBody caseInstance: CaseInstance,
        authentication: Authentication,
    ): ResponseEntity<Void> = caseInstanceProcessor.processInstance(authentication, caseInstance)

    @PostMapping("journalpost")
    fun postJournalPost(
        @Valid @RequestBody journalPostInstance: JournalPostInstance,
        authentication: Authentication,
    ): ResponseEntity<Void> = journalPostInstanceProcessor.processInstance(authentication, journalPostInstance)
}
