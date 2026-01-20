package no.novari.flyt.isygraving.gateway.dispatch.repository

import no.novari.flyt.isygraving.gateway.dispatch.model.DispatchReceiptEntity
import org.springframework.data.jpa.repository.JpaRepository

interface DispatchReceiptRepository : JpaRepository<DispatchReceiptEntity, String>
