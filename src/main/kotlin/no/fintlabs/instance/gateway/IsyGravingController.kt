package no.fintlabs.instance.gateway

import no.fintlabs.gateway.webinstance.InstanceProcessor
import no.fintlabs.instance.gateway.model.Sak
import no.fintlabs.webresourceserver.UrlPaths.EXTERNAL_API
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(EXTERNAL_API + "isygraving/instances")
class IsyGravingController(
    @param:Qualifier("sakInstanceProcessor")
    private val sakInstanceProcessor: InstanceProcessor<Sak>,
) {
    @PostMapping("/sak")
    fun sak(
        @RequestBody sak: Sak,
        @AuthenticationPrincipal authentication: Authentication,
    ): ResponseEntity<Void> {
        return sakInstanceProcessor.processInstance(authentication, sak)
    }
}
