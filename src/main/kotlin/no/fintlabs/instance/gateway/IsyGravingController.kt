package no.fintlabs.instance.gateway

import no.fintlabs.gateway.webinstance.InstanceProcessor
import no.fintlabs.instance.gateway.model.Journalenhet
import no.fintlabs.instance.gateway.model.Journalpost
import no.fintlabs.instance.gateway.model.Saksmappe
import no.fintlabs.instance.gateway.model.Saksstatus
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/isygraving/instances")
class IsyGravingController(
    @param:Qualifier("saksmappeInstanceProcessor")
    private val saksmappeInstanceProcessor: InstanceProcessor<Saksmappe>,
    @param:Qualifier("journalpostInstanceProcessor")
    private val journalpostInstanceProcessor: InstanceProcessor<Journalpost>,
) {
    @GetMapping("/sak/{instanceId}/status")
    fun getStatus(
        @PathVariable("instanceId") instanceId: String,
    ): Saksstatus {
        return Saksstatus(instanceId)
    }

    @PostMapping("/sak")
    fun sak(
        @RequestBody saksmappe: Saksmappe,
        @AuthenticationPrincipal authentication: Authentication,
    ): ResponseEntity<Void> {
        return saksmappeInstanceProcessor.processInstance(authentication, saksmappe)
    }

    @PostMapping("/journalpost")
    fun journalpost(
        @RequestBody journalpost: Journalpost,
        @AuthenticationPrincipal authentication: Authentication,
    ): Journalenhet {
        journalpostInstanceProcessor
            .processInstance(authentication, journalpost)
            .also {
                // TODO
                return Journalenhet(
                    kodeverdi = "",
                    kodebeskrivelse = "",
                    erGyldig = true,
                )
            }
    }
}
