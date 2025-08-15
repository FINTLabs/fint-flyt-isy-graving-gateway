package no.fintlabs.instance.gateway.mapping

import no.fintlabs.gateway.webinstance.InstanceMapper
import no.fintlabs.gateway.webinstance.model.instance.InstanceObject
import no.fintlabs.instance.gateway.model.Klasse
import no.fintlabs.instance.gateway.model.Saksmappe
import org.springframework.stereotype.Service

@Service
class SaksmappeInstantMappingService : InstanceMapper<Saksmappe> {
    override fun map(
        sourceApplicationId: Long,
        incomingInstance: Saksmappe,
        @Suppress("UNUSED_PARAMETER") persistFile: PersistFile,
    ): InstanceObject =
        with(incomingInstance) {
            val valuePerKey: Map<String, String> =
                buildMap {
                    putOrEmpty("sysId", sysId)
                    putOrEmpty("tittel", tittel)
                    putOrEmpty("offentligTittel", offentligTittel)
                    putOrEmpty("saksDato", saksdato)
                    putOrEmpty("skjermetTittel", skjermetTittel)
                    putOrEmpty("administrativEnhet", administrativEnhet)
                    putOrEmpty("kassasjonsDato", kassasjonsdato)
                    putOrEmpty("saksansvarligInit", saksansvarligInit)
                    putOrEmpty("tilgangsgruppeNavn", tilgangsgruppeNavn)

                    putOrEmpty("journalenhetKodeverdi", journalenhet.kodeverdi)
                    putOrEmpty("journalenhetKodebeskrivelse", journalenhet.kodebeskrivelse)
                    putOrEmpty("journalenhetErGyldig", journalenhet.erGyldig)

                    putOrEmpty("referanseArkivdelKodeverdi", referanseArkivdel.kodeverdi)
                    putOrEmpty("referanseArkivdelErGyldig", referanseArkivdel.erGyldig)

                    putOrEmpty("referanseEksternNoekkelNoekkel", referanseEksternNoekkel.noekkel)
                    putOrEmpty("referanseEksternNoekkelFagsystem", referanseEksternNoekkel.fagsystem)
                }

            val objectCollectionPerKey =
                mutableMapOf<String, Collection<InstanceObject>>(
                    "klasser" to klasse.map(::toInstanceObject),
                )

            InstanceObject(valuePerKey, objectCollectionPerKey)
        }

    private fun toInstanceObject(klasse: Klasse): InstanceObject {
        return InstanceObject(
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
}
