package no.fintlabs.instance.gateway.mapping

import no.fintlabs.gateway.webinstance.model.File
import no.fintlabs.gateway.webinstance.model.instance.InstanceObject
import no.fintlabs.instance.gateway.mapping.InstanceKeys.FILNAVN
import no.fintlabs.instance.gateway.mapping.InstanceKeys.FIL_ID
import no.fintlabs.instance.gateway.mapping.InstanceKeys.HOVEDDOKUMENT
import no.fintlabs.instance.gateway.mapping.InstanceKeys.MEDIATYPE
import no.fintlabs.instance.gateway.mapping.InstanceKeys.TITTEL
import no.fintlabs.instance.gateway.model.Dokument
import org.springframework.http.MediaType
import org.springframework.http.MediaTypeFactory
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class DokumenterMappingService {
    fun map(
        persistFile: (File) -> UUID,
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

    private fun mapAttachmentDocumentToInstanceObject(
        persistFile: (File) -> UUID,
        sourceApplicationId: Long,
        sourceApplicationInstanceId: String,
        dokument: Dokument,
    ): InstanceObject {
        return with(dokument) {
            val mediaType = getMediaType(this)
            val file =
                toFile(
                    sourceApplicationId = sourceApplicationId,
                    sourceApplicationInstanceId = sourceApplicationInstanceId,
                    dokument = this,
                    type = mediaType,
                )
            val fileId = persistFile(file)
            mapAttachmentDocumentAndFileIdToInstanceObject(dokument = this, mediaType = mediaType, fileId = fileId)
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
                    putOrEmpty(TITTEL, dokument.tittel)
                    putOrEmpty(FILNAVN, dokument.filnavn)
                    putOrEmpty(HOVEDDOKUMENT, dokument.hoveddokument)
                    putOrEmpty(MEDIATYPE, mediaType.toString())
                    putOrEmpty(FIL_ID, fileId)
                },
        )
    }

    private fun getMediaType(dokument: Dokument): MediaType {
        val mediaType = MediaTypeFactory.getMediaType(dokument.filnavn)
        return mediaType.orElseThrow {
            IllegalArgumentException("Could not determine media type from filnavn='${dokument.filnavn}'")
        }
    }

    private fun toFile(
        sourceApplicationId: Long,
        sourceApplicationInstanceId: String,
        dokument: Dokument,
        type: MediaType,
    ): File {
        return File(
            name = dokument.filnavn,
            type = type,
            sourceApplicationId = sourceApplicationId,
            sourceApplicationInstanceId = sourceApplicationInstanceId,
            encoding = "UTF-8",
            base64Contents = dokument.documentBase64,
        )
    }
}
