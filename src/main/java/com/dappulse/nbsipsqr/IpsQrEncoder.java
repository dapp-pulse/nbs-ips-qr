package com.dappulse.nbsipsqr;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Pattern;

/**
 * Encodes an {@link IpsQrPayload} into the pipe-delimited NBS IPS QR text string.
 *
 * Format: TAG:value|TAG:value|...|TAG:value
 * Spec: NBS IPS QR code specification v01, UTF-8, fields ordered per the standard.
 */
public class IpsQrEncoder {

    private static final String VERSION = "01";
    private static final String CHARSET = "1";
    private static final char DELIMITER = '|';

    private static final BigDecimal AMOUNT_MIN = new BigDecimal("0.01");
    private static final BigDecimal AMOUNT_MAX = new BigDecimal("99999999999999.99");

    private static final Pattern ACCOUNT_PATTERN = Pattern.compile("\\d{18}");
    private static final Pattern PAYMENT_CODE_PATTERN = Pattern.compile("\\d{3}");

    public String encode(IpsQrPayload payload) {
        String account = payload.creditorAccount().replace("-", "");
        validate(payload, account);

        StringBuilder sb = new StringBuilder();
        appendField(sb, "K", payload.identificationCode().name());
        appendField(sb, "V", VERSION);
        appendField(sb, "C", CHARSET);
        appendField(sb, "R", account);
        appendField(sb, "N", payload.creditorName());
        appendField(sb, "I", formatAmount(payload.currency(), payload.amount()));
        appendOptional(sb, "O", payload.debtorAccount());
        appendOptional(sb, "P", payload.debtorName());
        appendOptional(sb, "SF", payload.paymentCode());
        appendOptional(sb, "S", payload.paymentPurpose());

        if (!sb.isEmpty() && sb.charAt(sb.length() - 1) == DELIMITER) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }

    private void validate(IpsQrPayload payload, String account) {
        if (!ACCOUNT_PATTERN.matcher(account).matches()) {
            throw new IllegalArgumentException(
                    "creditorAccount (R) must be exactly 18 digits after removing dashes, got: " + payload.creditorAccount());
        }

        BigDecimal amount = payload.amount();
        if (amount.compareTo(AMOUNT_MIN) < 0 || amount.compareTo(AMOUNT_MAX) > 0) {
            throw new IllegalArgumentException(
                    "amount must be between " + AMOUNT_MIN + " and " + AMOUNT_MAX + ", got: " + amount);
        }

        String paymentCode = payload.paymentCode();
        if (paymentCode != null && !paymentCode.isBlank() && !PAYMENT_CODE_PATTERN.matcher(paymentCode).matches()) {
            throw new IllegalArgumentException(
                    "paymentCode (SF) must be exactly 3 digits, got: " + paymentCode);
        }
    }

    // NBS format: {CURRENCY}{integer},{fraction} — decimal comma, no thousands separator
    private String formatAmount(String currency, BigDecimal amount) {
        String plain = amount.setScale(2, RoundingMode.HALF_UP).toPlainString();
        return currency + plain.replace('.', ',');
    }

    private void appendField(StringBuilder sb, String tag, String value) {
        sb.append(tag).append(':').append(value).append(DELIMITER);
    }

    private void appendOptional(StringBuilder sb, String tag, String value) {
        if (value != null && !value.isBlank()) {
            appendField(sb, tag, value);
        }
    }
}
