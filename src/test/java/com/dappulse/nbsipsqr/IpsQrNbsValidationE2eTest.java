package com.dappulse.reportgenerator.qr;

import static com.dappulse.nbsipsqr.IpsQrPayload.IdentificationCode.PR;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.dappulse.nbsipsqr.IpsQrEncoder;
import com.dappulse.nbsipsqr.IpsQrPayload;

/**
 * Validates generated IPS QR strings against the official NBS validator API.
 * Disabled by default — run manually during development to verify spec compliance.
 *
 * Endpoint: POST https://nbs.rs/QRcode/api/qr/v1/validate
 * Body: plain text IPS QR string
 */
@Disabled("Requires network access to ips.nbs.rs — run manually to verify NBS spec compliance")
class IpsQrNbsValidationE2eTest {

    private static final String NBS_VALIDATE_URL = "https://nbs.rs/QRcode/api/qr/v1/validate";

    private final IpsQrEncoder encoder = new IpsQrEncoder();
    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @Test
    void mandatoryFieldsOnlyPassesNbsValidation() throws Exception {
        String qrString = encoder.encode(new IpsQrPayload(
                PR, "845000000040484987", "Acme d.o.o., Beograd", "RSD",
                new BigDecimal("3596.13"), null, null, null, null));

        var response = validate(qrString);

        assertThat(response.statusCode())
                .as("NBS validate response for: %s\nBody: %s", qrString, response.body())
                .isEqualTo(200);
    }

    @Test
    void allFieldsPassNbsValidation() throws Exception {
        String qrString = encoder.encode(new IpsQrPayload(
                PR, "845000000040484987", "Acme d.o.o., Beograd", "RSD",
                new BigDecimal("3596.13"),
                null, "Marko Marković, Bulevar 12, Novi Sad", "289", "Proforma faktura 2024-001"));

        var response = validate(qrString);

        assertThat(response.statusCode())
                .as("NBS validate response for: %s\nBody: %s", qrString, response.body())
                .isEqualTo(200);
    }

    @Test
    void cyrillicPurposePassesNbsValidation() throws Exception {
        String qrString = encoder.encode(new IpsQrPayload(
                PR, "845000000040484987", "Предузеће д.о.о., Београд", "RSD",
                new BigDecimal("1500.00"),
                null, null, "289", "Профактура 2024-001"));

        var response = validate(qrString);

        assertThat(response.statusCode())
                .as("NBS validate response for: %s\nBody: %s", qrString, response.body())
                .isEqualTo(200);
    }

    // --- helper ---

    private HttpResponse<String> validate(String qrString) throws Exception {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(NBS_VALIDATE_URL))
                .header("Content-Type", "text/plain; charset=UTF-8")
                .timeout(Duration.ofSeconds(10))
                .POST(HttpRequest.BodyPublishers.ofString(qrString))
                .build();
        return http.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
