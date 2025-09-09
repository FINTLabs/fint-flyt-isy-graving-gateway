package no.fintlabs.instance.gateway.mapping

import net.datafaker.Faker
import no.fintlabs.instance.gateway.mapping.InstanceKeys.ADRESSE
import no.fintlabs.instance.gateway.mapping.InstanceKeys.NAVN
import no.fintlabs.instance.gateway.mapping.InstanceKeys.ORG_NR
import no.fintlabs.instance.gateway.mapping.InstanceKeys.POSTNUMMER
import no.fintlabs.instance.gateway.model.Mottaker
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MottakerMappingServiceTest {
    private val faker: Faker = Faker()

    private val service = MottakerMappingService()

    @Test
    fun `map copies all fields`() {
        val mottaker =
            Mottaker(
                navn = faker.name().name(),
                adresse = faker.address().streetAddress(),
                postnummer = faker.address().zipCode(),
                orgNr = faker.number().randomNumber().toString(),
            )

        val result = service.map(mottaker)

        assertThat(result.valuePerKey)
            .containsEntry(NAVN, mottaker.navn)
            .containsEntry(ADRESSE, mottaker.adresse)
            .containsEntry(POSTNUMMER, mottaker.postnummer)
            .containsEntry(ORG_NR, mottaker.orgNr)
    }
}
