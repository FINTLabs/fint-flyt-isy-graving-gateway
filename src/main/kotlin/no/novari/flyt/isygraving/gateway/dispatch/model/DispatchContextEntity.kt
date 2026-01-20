package no.novari.flyt.isygraving.gateway.dispatch.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "dispatch_context")
class DispatchContextEntity(
    @Id
    @Column(name = "id")
    var id: String = "",
    @Column(name = "source_application_integration_id")
    var sourceApplicationIntegrationId: String = "",
    @Column(name = "source_application_instance_id")
    var sourceApplicationInstanceId: String = "",
    @Column(name = "tenant")
    var tenant: String = "",
    @Column(name = "case_id")
    var caseId: String = "",
    @Column(name = "case_archive_guid")
    var caseArchiveGuid: String = "",
    @Column(name = "callback_url")
    var callbackUrl: String = "",
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DispatchContextEntity) return false
        if (id.isBlank() || other.id.isBlank()) return false
        return id == other.id
    }

    override fun hashCode(): Int = if (id.isBlank()) System.identityHashCode(this) else id.hashCode()
}
