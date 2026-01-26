package no.novari.flyt.isygraving.gateway.instance

import no.novari.flyt.isygraving.gateway.instance.mapping.MissingMainDocumentException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class InstanceExceptionHandler {
    @ExceptionHandler(MissingMainDocumentException::class)
    fun handleMissingMainDocument(ex: MissingMainDocumentException): ResponseEntity<Map<String, String>> =
        ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            mapOf("message" to (ex.message ?: "Main document is required")),
        )
}
