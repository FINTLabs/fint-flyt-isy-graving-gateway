package no.fintlabs.instance.gateway.mapping

import no.fintlabs.gateway.webinstance.InstanceMapper
import no.fintlabs.gateway.webinstance.model.File
import no.fintlabs.gateway.webinstance.model.instance.InstanceObject
import no.fintlabs.instance.gateway.extension.toId
import no.fintlabs.instance.gateway.model.Avskrivning
import no.fintlabs.instance.gateway.model.Dokument
import no.fintlabs.instance.gateway.model.Journalpost
import no.fintlabs.instance.gateway.model.Korrespondansepart
import org.springframework.http.MediaType
import org.springframework.http.MediaTypeFactory
import org.springframework.stereotype.Service
import java.util.UUID
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@Service
class JournalpostInstantMappingService : InstanceMapper<Journalpost> {
    override fun map(
        sourceApplicationId: Long,
        incomingInstance: Journalpost,
        persistFile: PersistFile,
    ): InstanceObject {
        return with(incomingInstance) {
            val dokumenterInstanceObjects =
                mapAttachmentDocumentsToInstanceObjects(
                    persistFile = persistFile,
                    sourceApplicationId = sourceApplicationId,
                    sourceApplicationInstanceId = incomingInstance.saksnr.toId(), // FIXME: Add correct value
                    dokumenter = dokumenter,
                )

            val valuePerKey: Map<String, String> =
                buildMap {
                    putOrEmpty("journalDato", journaldato)
                    putOrEmpty("journalposttypeKodeverdi", journalposttype.kodeverdi)
                    putOrEmpty("journalposttypeErGyldig", journalposttype.erGyldig)
                    putOrEmpty("dokumentetsDato", dokumentetsDato)
                    putOrEmpty("journalstatusKodeverdi", journalstatus.kodeverdi)
                    putOrEmpty("journalstatusErGyldig", journalstatus.erGyldig)
                    putOrEmpty("tittel", tittel)
                    putOrEmpty("skjermetTittel", skjermetTittel)
                    putOrEmpty("forfallsdato", forfallsdato)
                    putOrEmpty("saksnrSaksaar", saksnr.saksaar)
                    putOrEmpty("saksnrSakssekvensnummer", saksnr.sakssekvensnummer)
                    putOrEmpty("referanseEksternNoekkelNoekkel", referanseEksternNoekkel.noekkel)
                    putOrEmpty("referanseEksternNoekkelFagsystem", referanseEksternNoekkel.fagsystem)
                }

            val objectCollectionPerKey =
                mutableMapOf<String, Collection<InstanceObject>>(
                    "korrespondansepart" to korrespondansepart.map(::toInstanceObject),
                    "referanseAvskrivninger" to referanseAvskrivninger.map(::toInstanceObject),
                    "dokumenter" to dokumenterInstanceObjects,
                )

            InstanceObject(valuePerKey, objectCollectionPerKey)
        }
    }

    private fun toInstanceObject(k: Korrespondansepart): InstanceObject {
        return InstanceObject(
            valuePerKey =
                buildMap {
                    putOrEmpty("korrespondanseparttypeKodeverdi", k.korrespondanseparttype.kodeverdi)
                    putOrEmpty("korrespondanseparttypeErGyldig", k.korrespondanseparttype.erGyldig)
                    putOrEmpty("skjermetKorrespondansepart", k.skjermetKorrespondansepart)
                    putOrEmpty("fristBesvarelse", k.fristBesvarelse)
                    putOrEmpty("kontaktNavn", k.kontakt.navn)
                    putOrEmpty("kontaktOrganisasjonsnummer", k.kontakt.organisasjonsnummer)
                },
        )
    }

    private fun toInstanceObject(a: Avskrivning): InstanceObject {
        return InstanceObject(
            valuePerKey =
                buildMap {
                    putOrEmpty("avskrivningsdato", a.avskrivningsdato)
                    putOrEmpty("avskrivningsmaate_kodeverdi", a.avskrivningsmaate.kodeverdi)
                    putOrEmpty("avskrivningsmaate_er_gyldig", a.avskrivningsmaate.erGyldig)
                },
        )
    }

    private fun mapAttachmentDocumentsToInstanceObjects(
        persistFile: PersistFile,
        sourceApplicationId: Long,
        sourceApplicationInstanceId: String,
        dokumenter: List<Dokument>,
    ): List<InstanceObject> {
        return dokumenter.map {
            mapAttachmentDocumentToInstanceObject(
                persistFile = persistFile,
                sourceApplicationId = sourceApplicationId,
                sourceApplicationInstanceId = sourceApplicationInstanceId,
                dokument = it,
            )
        }
    }

    private fun getMediaType(dokument: Dokument): MediaType {
        val mediaType = MediaTypeFactory.getMediaType(dokument.fil.filnavn)
        return mediaType.orElseThrow {
            IllegalArgumentException("No media type found for fileName=${dokument.fil.filnavn}")
        }
    }

    @OptIn(ExperimentalEncodingApi::class)
    private fun toFile(
        sourceApplicationId: Long,
        sourceApplicationInstanceId: String,
        dokument: Dokument,
        type: MediaType,
    ): File {
        return File(
            name = dokument.fil.filnavn,
            type = type,
            sourceApplicationId = sourceApplicationId,
            sourceApplicationInstanceId = sourceApplicationInstanceId,
            encoding = "UTF-8",
            base64Contents = Base64.encode(dokument.fil.base64),
        )
    }

    private fun mapAttachmentDocumentToInstanceObject(
        persistFile: PersistFile,
        sourceApplicationId: Long,
        sourceApplicationInstanceId: String,
        dokument: Dokument,
    ): InstanceObject {
        return with(dokument) {
            val mediaType = getMediaType(this)
            val file = toFile(sourceApplicationId, sourceApplicationInstanceId, this, mediaType)
            val fileId = persistFile(file)
            mapAttachmentDocumentAndFileIdToInstanceObject(this, mediaType, fileId)
        }
    }

    private fun mapAttachmentDocumentAndFileIdToInstanceObject(
        dokument: Dokument,
        mediaType: MediaType,
        fileId: UUID,
    ): InstanceObject {
        return InstanceObject(
            valuePerKey =
                buildMap {
                    putOrEmpty("tittel", dokument.fil.filnavn)
                    putOrEmpty("filnavn", dokument.fil.filnavn)
                    putOrEmpty("mediatype", mediaType.toString())
                    putOrEmpty("fil", fileId)
                },
        )
    }
}
