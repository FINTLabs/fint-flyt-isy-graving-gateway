package no.fintlabs.model

data class Saksmappe(
    val saksdato: String,
    val tittel: String,
    val offentligTittel: String,
    val skjermetTittel: Boolean,
    val referanseArkivdel: KodeverdiGyldig,
    val journalenhet: Journalenhet,
    val kassasjonsdato: String,
    val administrativEnhet: String?,
    val saksansvarligInit: String,
    val tilgangsgruppeNavn: String,
    val klasse: List<Klasse>,
    val referanseEksternNoekkel: ReferanseEksternNoekkel
)