package com.dappulse.nbsipsqr;

import static com.dappulse.nbsipsqr.IpsQrPayload.IdentificationCode.EK;
import static com.dappulse.nbsipsqr.IpsQrPayload.IdentificationCode.PR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class IpsQrEncoderTest {

    private final IpsQrEncoder encoder = new IpsQrEncoder();

    private static final String ACCOUNT_PLAIN = "111123456789012383";
    private static final String ACCOUNT_DASHES = "111-1234567890123-83";
    private static final String SHORT_ACCOUNT = "12332132118";
    private static final String NORMALIZED_SHORT_ACCOUNT = "123000000032132118";

    // --- Encoding ---

    @Test
    void encode_minimalPayload_producesCorrectString() {
        IpsQrPayload payload = createIpsQrPayload(ACCOUNT_PLAIN, "1500.00");

        assertThat(encoder.encode(payload))
                .isEqualTo("K:PR|V:01|C:1|R:" + ACCOUNT_PLAIN + "|N:Acme d.o.o.|I:RSD1500,00");
    }

    @Test
    void encode_allFieldsProvided_producesCorrectString() {
        IpsQrPayload payload = new IpsQrPayload(
                EK,
                ACCOUNT_PLAIN,
                "Acme d.o.o., Beograd",
                "RSD",
                new BigDecimal("3596.13"),
                "222987654321098714",
                "Pera Peric, Beograd",
                "289",
                "Uplata po fakturi 123"
        );

        assertThat(encoder.encode(payload))
                .isEqualTo("K:EK|V:01|C:1|R:" + ACCOUNT_PLAIN + "|N:Acme d.o.o., Beograd|I:RSD3596,13"
                        + "|O:222987654321098714|P:Pera Peric, Beograd|SF:289|S:Uplata po fakturi 123");
    }

    @Test
    void encode_accountWithDashes_stripsAndAccepts() {
        IpsQrPayload payload = createIpsQrPayload(ACCOUNT_DASHES, "100.00");

        String result = encoder.encode(payload);

        assertThat(result).contains("R:" + ACCOUNT_PLAIN);
        assertThat(result).doesNotContain("-");
    }

    @Test
    void encode_shortAccount_normalizesWithLeftPaddedAccountNumber() {
        IpsQrPayload payload = createIpsQrPayload(SHORT_ACCOUNT, "100.00");

        assertThat(encoder.encode(payload)).contains("R:" + NORMALIZED_SHORT_ACCOUNT);
    }

    @Test
    void encode_debtorAccount_normalizesOptionalAccount() {
        var payload = new IpsQrPayload(PR, ACCOUNT_PLAIN, "Acme d.o.o.", "RSD", new BigDecimal("1.00"),
                SHORT_ACCOUNT, "Pera Peric", null, null);

        assertThat(encoder.encode(payload)).contains("|O:" + NORMALIZED_SHORT_ACCOUNT);
    }

    @Test
    void usesIdentificationCodeName() {
        var payload = new IpsQrPayload(EK, ACCOUNT_PLAIN, "Acme d.o.o.", "RSD", new BigDecimal("1.00"),
                null, null, null, null);
        assertThat(encoder.encode(payload)).startsWith("K:EK|");
    }

    @Test
    void alwaysInjectsVersionAndCharset() {
        String encoded = encoder.encode(createIpsQrPayload(ACCOUNT_PLAIN, "1.00"));
        assertThat(encoded).contains("|V:01|").contains("|C:1|");
    }

    @Test
    void noTrailingDelimiter() {
        assertThat(encoder.encode(createIpsQrPayload(ACCOUNT_PLAIN, "50.00"))).doesNotEndWith("|");
    }

    @Test
    void skipNullOptionalFields() {
        assertOptionalFieldsAbsent(null, null, null, null);
    }

    @Test
    void skipBlankOptionalFields() {
        assertOptionalFieldsAbsent("  ", "", "   ", "");
    }

    // --- Amount formatting ---

    @Test
    void formatsWholeAmountWithTwoDecimalPlaces() {
        assertThat(encoder.encode(createIpsQrPayload(ACCOUNT_PLAIN, "9999"))).contains("I:RSD9999,00");
    }

    @Test
    void formatsDecimalAmountWithComma() {
        assertThat(encoder.encode(createIpsQrPayload(ACCOUNT_PLAIN, "3596.13"))).contains("I:RSD3596,13");
    }

    @Test
    void roundsAmountHalfUp() {
        assertThat(encoder.encode(createIpsQrPayload(ACCOUNT_PLAIN, "10.005"))).contains("I:RSD10,01");
    }

    // --- Validation ---

    @Test
    void validate_accountWithLetters_throws() {
        IpsQrPayload payload = createIpsQrPayload("111-123456789012A-83", "100.00");

        assertThatThrownBy(() -> encoder.encode(payload))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("IQE_001:");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "12345", // 5-digits, too short.
            "1234567890123456789" // 19-digits, to long.
    })
    void normalizeAccount_rejectsAccountWithWrongDigitCount(String account) {
        assertThatThrownBy(() -> IpsQrEncoder.normalizeAccount(account))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("IQE_002:");
    }

    @Test
    void normalizeAccount_rejectsNullAccount() {
        assertThatThrownBy(() -> IpsQrEncoder.normalizeAccount(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageStartingWith("IQE_004:");
    }

    @Test
    void validate_accountWithInvalidControlDigits_throws() {
        IpsQrPayload payload = createIpsQrPayload("111123456789012311", "100.00");

        assertThatThrownBy(() -> encoder.encode(payload))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("IQE_003:");
    }

    @Test
    void normalizeAccount_acceptsWhitespaceAndDashes() {
        assertThat(IpsQrEncoder.normalizeAccount(" 123-321321-18 "))
                .isEqualTo(NORMALIZED_SHORT_ACCOUNT);
    }

    @Test
    void validate_amountBelowMinimum_throws() {
        assertThatThrownBy(() -> encoder.encode(createIpsQrPayload(ACCOUNT_PLAIN, "0.00")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("IQE_005:");
    }

    @Test
    void rejectsNegativeAmount() {
        assertThatThrownBy(() -> encoder.encode(createIpsQrPayload(ACCOUNT_PLAIN, "-1.00")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("IQE_005:");
    }

    @Test
    void rejectsAmountAboveMaximum() {
        assertThatThrownBy(() -> encoder.encode(createIpsQrPayload(ACCOUNT_PLAIN, "100000000000000.00")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("IQE_005:");
    }

    @Test
    void acceptsMinimumAmount() {
        assertThat(encoder.encode(createIpsQrPayload(ACCOUNT_PLAIN, "0.01"))).contains("I:RSD0,01");
    }

    @Test
    void acceptsMaximumAmount() {
        assertThat(encoder.encode(createIpsQrPayload(ACCOUNT_PLAIN, "99999999999999.99")))
                .contains("I:RSD99999999999999,99");
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "12", "1234", "ab3", "1 2"})
    void rejectsPaymentCodeNotExactlyThreeDigits(String sf) {
        var payload = new IpsQrPayload(PR, ACCOUNT_PLAIN, "Acme d.o.o.", "RSD", new BigDecimal("1.00"),
                null, null, sf, null);
        assertThatThrownBy(() -> encoder.encode(payload))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("IQE_006:");
    }

    @ParameterizedTest
    @ValueSource(strings = {"189", "263", "289", "000"})
    void acceptsValidThreeDigitPaymentCode(String sf) {
        var payload = new IpsQrPayload(PR, ACCOUNT_PLAIN, "Acme d.o.o.", "RSD", new BigDecimal("1.00"),
                null, null, sf, null);
        assertThat(encoder.encode(payload)).contains("|SF:" + sf);
    }

    // --- Helpers ---

    private void assertOptionalFieldsAbsent(String debtor, String debtorAccount, String paymentCode, String purpose) {
        var payload = new IpsQrPayload(PR, ACCOUNT_PLAIN, "Acme d.o.o.", "RSD", new BigDecimal("1.00"),
                debtor, debtorAccount, paymentCode, purpose);
        assertThat(encoder.encode(payload))
                .doesNotContain("|O:")
                .doesNotContain("|P:")
                .doesNotContain("|SF:")
                .doesNotContain("|S:");
    }

    private IpsQrPayload createIpsQrPayload(String account, String amount) {
        return new IpsQrPayload(
                PR,
                account,
                "Acme d.o.o.",
                "RSD",
                new BigDecimal(amount),
                null, null, null, null
        );
    }
}
