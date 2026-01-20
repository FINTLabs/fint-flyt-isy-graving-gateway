package no.novari.flyt.isygraving.gateway.instance

import no.novari.flyt.isygraving.gateway.instance.mapping.MissingMainDocumentException
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals

class InstanceExceptionHandlerTest {
    private val handler = InstanceExceptionHandler()

    @Test
    fun `returns bad request with message when main document is missing`() {
        val exception = MissingMainDocumentException("Main document is required")

        val response = handler.handleMissingMainDocument(exception)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals(mapOf("message" to "Main document is required"), response.body)
    }
}
