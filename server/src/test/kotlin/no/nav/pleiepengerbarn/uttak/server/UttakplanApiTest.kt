package no.nav.pleiepengerbarn.uttak.server

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.test.context.ActiveProfiles
import java.net.URI

private const val UTTAKSPLAN_PATH = "/uttaksplan"

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class UttakplanApiTest(@Autowired val restTemplate: TestRestTemplate) {

    @Test
    fun `Opprett uttaksplan`() {

        val requestBody = """
            {
                "sakId": "123",
                "behandlingId": "474abb91-0e61-4459-ba5f-7e960d45c165",
                "søknadsperioder": [
                    "2020-01-01/2020-03-31"
                ],
                "arbeidsforhold": [
                    {
                        "arbeidstype": "ARBEIDSGIVER",
                        "organisasjonsnummer": "999999999",
                        "arbeidsforholdId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                        "perioder": {
                            "2020-01-01/2020-03-31": {
                                "inntekt": 1000,
                                "arbeidsprosent": 0
                            }
                        }
                    }
                ],
                "tilsynsbehov": {
                    "2020-01-01/2020-03-31": {
                        "prosent": "PROSENT_100"
                    }
                },
                "medlemskap": {
                    "2020-01-01/2020-03-31": {
                        "frivilligMedlem": true
                    }
                }
            }
        """.trimIndent()

        val request = RequestEntity
                .post(URI.create(UTTAKSPLAN_PATH))
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)



        val response = restTemplate.exchange(request, Void::class.java)

        assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
    }

}