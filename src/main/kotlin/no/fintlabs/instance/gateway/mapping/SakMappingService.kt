package no.fintlabs.instance.gateway.mapping

import no.fintlabs.gateway.webinstance.InstanceMapper
import no.fintlabs.gateway.webinstance.model.instance.InstanceObject
import no.fintlabs.instance.gateway.model.Sak
import org.springframework.stereotype.Service

@Service
class SakMappingService : InstanceMapper<Sak> {
    override fun map(
        sourceApplicationId: Long,
        incomingInstance: Sak,
        @Suppress("UNUSED_PARAMETER") persistFile: PersistFile,
    ): InstanceObject =
        with(incomingInstance) {
            val valuePerKey: Map<String, String> =
                buildMap {
                    putOrEmpty("saksId", saksId)
                }

            val objectCollectionPerKey =
                mutableMapOf<String, Collection<InstanceObject>>(
                    "journalpost" to klasse.map(::toInstanceObject),
                )

            InstanceObject(valuePerKey, objectCollectionPerKey)
        }

    private fun toInstanceObject(klasse: Klasse): InstanceObject =
        InstanceObject(
            valuePerKey =
                buildMap {
                    putOrEmpty("rekkefoelge", klasse.rekkefoelge)
                    putOrEmpty("klasse_id", klasse.klasseID)
                    putOrEmpty("skjermetKlasse", klasse.skjermetKlasse)
                    putOrEmpty("tittel", klasse.tittel)
                    putOrEmpty("ledetekst", klasse.ledetekst)
                    putOrEmpty("klassifikasjonssystemKodebeskrivelse", klasse.klassifikasjonssystem.kodebeskrivelse)
                },
        )
}
