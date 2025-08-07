package no.fintlabs

import no.fintlabs.model.Journalenhet
import no.fintlabs.model.Journalpost
import no.fintlabs.model.Saksmappe
import no.fintlabs.model.Saksstatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/isygraving/instances")
class IsyGravingController {

    @GetMapping("/{instanceId}/status")
    fun getStatus(
        @PathVariable("instanceId") instanceId: String,
    ) : Saksstatus {
        return Saksstatus(instanceId)
    }

    @PostMapping("/sak")
    fun sak(
        @RequestBody request: Saksmappe
    ) : ResponseEntity<Void> {
        return ResponseEntity.accepted().body(null)
    }

    @PostMapping("/journalpost")
    fun journalpost(
        @RequestBody request: Journalpost
    ) : ResponseEntity<Journalenhet> {
        return ResponseEntity.accepted().body(null)
    }


}