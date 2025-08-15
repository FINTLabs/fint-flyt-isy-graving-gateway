package no.fintlabs.instance.gateway.model

data class Korrespondansepart(
    val korrespondanseparttype: KodeverdiGyldig,
    val skjermetKorrespondansepart: Boolean,
    val fristBesvarelse: String,
    val kontakt: Kontakt
)