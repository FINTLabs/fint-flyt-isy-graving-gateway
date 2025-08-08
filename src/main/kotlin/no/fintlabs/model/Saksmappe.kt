package no.fintlabs.model

data class Saksmappe(
    val sysId: String,
    val administrativEnhet: String,
    val kassasjonsdato: String,
    val offentligTittel: String,
    val saksansvarligInit: String,
    val saksdato: String,
    val skjermetTittel: String,
    val tilgangsgruppeNavn: String,
    val tittel: String,

    val journalenhet: Journalenhet,
    val klasse: List<Klasse>,
    val referanseArkivdel: ReferanseArkivdel,
    val referanseEksternNoekkel: ReferanseEksternNoekkel
)