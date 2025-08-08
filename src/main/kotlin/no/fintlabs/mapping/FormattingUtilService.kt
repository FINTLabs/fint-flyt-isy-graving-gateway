package no.fintlabs.mapping

import org.springframework.stereotype.Service
import java.util.*

@Service
class FormattingUtilsService {
    fun formatEmail(email: String): String {
        return email.trim { it <= ' ' }.lowercase(Locale.getDefault())
    }

    fun extractEmailDomain(email: String?): String {
        if (email == null || !email.contains("@")) {
            return "Invalid email"
        }
        return email.substring(email.indexOf("@") + 1).lowercase(Locale.getDefault())
    }

}