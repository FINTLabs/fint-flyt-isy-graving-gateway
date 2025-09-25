package no.fintlabs.instance.gateway.mapping

import no.fintlabs.gateway.webinstance.model.instance.InstanceObject
import no.fintlabs.instance.gateway.mapping.InstanceKeys.ADRESSE
import no.fintlabs.instance.gateway.mapping.InstanceKeys.NAVN
import no.fintlabs.instance.gateway.mapping.InstanceKeys.ORG_NR
import no.fintlabs.instance.gateway.mapping.InstanceKeys.POSTNUMMER
import no.fintlabs.instance.gateway.model.Mottaker
import org.springframework.stereotype.Service

@Service
class MottakerMappingService {
    fun map(mottaker: Mottaker): InstanceObject {
        return InstanceObject(
            valuePerKey =
                buildMap {
                    putOrEmpty(NAVN, mottaker.navn)
                    putOrEmpty(ADRESSE, mottaker.adresse)
                    putOrEmpty(POSTNUMMER, mottaker.postnummer)
                    putOrEmpty(ORG_NR, mottaker.orgNr)
                },
        )
    }
}
