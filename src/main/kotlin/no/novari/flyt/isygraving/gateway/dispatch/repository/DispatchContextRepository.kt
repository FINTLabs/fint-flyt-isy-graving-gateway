package no.novari.flyt.isygraving.gateway.dispatch.repository

import no.novari.flyt.isygraving.gateway.dispatch.model.DispatchContextEntity
import org.springframework.data.jpa.repository.JpaRepository

interface DispatchContextRepository : JpaRepository<DispatchContextEntity, String>
