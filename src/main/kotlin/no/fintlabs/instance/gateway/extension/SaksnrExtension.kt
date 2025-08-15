package no.fintlabs.instance.gateway.extension

import no.fintlabs.instance.gateway.model.Saksnr

fun Saksnr.toId() = "${this.saksaar}-${this.sakssekvensnummer}"
