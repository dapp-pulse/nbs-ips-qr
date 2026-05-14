package com.dappulse.nbsipsqr;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class IpsQrEncoderTest {

    private final IpsQrEncoder encoder = new IpsQrEncoder();

    // 18-digit account, once as plain digits and once formatted with dashes
    private static final String ACCOUNT_PLAIN = "111123456789012311";
    private static final String ACCOUNT_DASHES = "111-1234567890123-11";

    @Test
    void encode_minimalPayload_producesCorrectString() {
        IpsQrPayload payload = createIpsQrPayload(ACCOUNT_PLAIN, "1500.00");

        String result = encoder.encode(payload);

        assertEquals(
                "K:PR|V:01|C:1|R:" + ACCOUNT_PLAIN + "|N:Acme d.o.o.|I:RSD1500,00",
                result
        );
    }

    @Test
    void encode_allFieldsProvided_producesCorrectString() {
        IpsQrPayload payload = new IpsQrPayload(
                IpsQrPayload.IdentificationCode.EK,
                ACCOUNT_PLAIN,
                "Acme d.o.o., Beograd",
                "RSD",
                new BigDecimal("3596.13"),
                "222987654321098700",
                "Pera Peric, Beograd",
                "289",
                "Uplata po fakturi 123"
        );

        String result = encoder.encode(payload);

        assertEquals(
                "K:EK|V:01|C:1|R:" + ACCOUNT_PLAIN + "|N:Acme d.o.o., Beograd|I:RSD3596,13"
                        + "|O:222987654321098700|P:Pera Peric, Beograd|SF:289|S:Uplata po fakturi 123",
                result
        );
    }

    @Test
    void encode_accountWithDashes_stripsAndAccepts() {
        IpsQrPayload payload = createIpsQrPayload(ACCOUNT_DASHES, "100.00");

        String result = encoder.encode(payload);

        assertTrue(result.contains("R:" + ACCOUNT_PLAIN), "dashes must be stripped from account in output");
        assertFalse(result.contains("-"), "output must not contain any dashes");
    }

    @Test
    void validate_accountWithDashesThatYieldWrongDigitCount_throws() {
        // "123-456789-01" → "12345678901" = 11 digits, not 18
        IpsQrPayload payload = createIpsQrPayload("123-456789-01", "100.00");

        assertThrows(IllegalArgumentException.class, () -> encoder.encode(payload));
    }

    @Test
    void validate_amountBelowMinimum_throws() {
        IpsQrPayload payload = createIpsQrPayload(ACCOUNT_PLAIN, "0.00");

        assertThrows(IllegalArgumentException.class, () -> encoder.encode(payload));
    }

    @Test
    void validate_paymentCodeNotThreeDigits_throws() {
        IpsQrPayload payload = new IpsQrPayload(
                IpsQrPayload.IdentificationCode.PR,
                ACCOUNT_PLAIN,
                "Acme d.o.o.",
                "RSD",
                new BigDecimal("100.00"),
                null, null, "12", null
        );

        assertThrows(IllegalArgumentException.class, () -> encoder.encode(payload));
    }

    private IpsQrPayload createIpsQrPayload(String account, String amount) {
        return new IpsQrPayload(
                IpsQrPayload.IdentificationCode.PR,
                account,
                "Acme d.o.o.",
                "RSD",
                new BigDecimal(amount),
                null, null, null, null
        );
    }
}
