package no.fintlabs.model

data class Klasse(
    val rekkefoelge: Int,
    val klassifikasjonssystem: Klassifikasjonssystem,
    val klasseID: String,
    val skjermetKlasse: Boolean,
    val ledetekst: String,
    val tittel: String
)