package no.novari.flyt.isygraving.gateway.instance

import no.novari.flyt.isygraving.gateway.instance.mapping.MissingMainDocumentException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.FieldError
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.MethodArgumentNotValidException

@RestControllerAdvice
class InstanceExceptionHandler {
    @ExceptionHandler(MissingMainDocumentException::class)
    fun handleMissingMainDocument(ex: MissingMainDocumentException): ResponseEntity<Map<String, String>> =
        ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            mapOf("message" to (ex.message ?: "Main document is required")),
        )

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException): ResponseEntity<Map<String, Any>> {
        val fieldErrors =
            ex.bindingResult
                .fieldErrors
                .map { error ->
                    mapOf(
                        "field" to error.field,
                        "message" to (error.defaultMessage ?: "Invalid value"),
                    )
                }
        val globalErrors =
            ex.bindingResult
                .globalErrors
                .filterNot { it is FieldError }
                .map { error ->
                    mapOf(
                        "object" to error.objectName,
                        "message" to (error.defaultMessage ?: "Invalid value"),
                    )
                }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            mapOf(
                "message" to "Invalid request. Missing required fields or invalid values.",
                "errors" to (fieldErrors + globalErrors),
            ),
        )
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleJsonParseException(ex: HttpMessageNotReadableException): ResponseEntity<Map<String, String>> {
        val detail = ex.mostSpecificCause?.message?.take(500) ?: "Unable to parse JSON."
        val hint =
            when {
                detail.contains("Unrecognized token 'http") || detail.contains("Unrecognized token 'https") ->
                    "String values must be quoted, e.g. \"https://...\"."
                else -> null
            }
        val body =
            buildMap {
                put("message", "Invalid JSON.")
                put("detail", detail)
                if (hint != null) {
                    put("hint", hint)
                }
            }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body)
    }
}
