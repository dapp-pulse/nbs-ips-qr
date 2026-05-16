package com.dappulse.nbsipsqr;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Pattern;

/**
 * Encodes an {@link IpsQrPayload} into the pipe-delimited NBS IPS QR text string.
 * <p>
 * Format: TAG:value|TAG:value|...|TAG:value
 * Spec: NBS IPS QR code specification v01, UTF-8, fields ordered per the standard.
 */
public class IpsQrEncoder {

    private static final String VERSION = "01";
    private static final String CHARSET = "1";
    private static final char DELIMITER = '|';

    private static final BigDecimal AMOUNT_MIN = new BigDecimal("0.01");
    private static final BigDecimal AMOUNT_MAX = new BigDecimal("99999999999999.99");

    private static final int BANK_CODE_LENGTH = 3;
    private static final int ACCOUNT_NUMBER_LENGTH = 13;
    private static final int CONTROL_NUMBER_LENGTH = 2;
    private static final int NORMALIZED_ACCOUNT_LENGTH =
            BANK_CODE_LENGTH + ACCOUNT_NUMBER_LENGTH + CONTROL_NUMBER_LENGTH;

    private static final Pattern ACCOUNT_PATTERN = Pattern.compile("[0-9]+");
    private static final Pattern PAYMENT_CODE_PATTERN = Pattern.compile("\\d{3}");

    public String encode(IpsQrPayload payload) {
        String creditorAccount = normalizeAccount(payload.creditorAccount());
        String debtorAccount = payload.debtorAccount();
        if (debtorAccount != null && !debtorAccount.isBlank()) {
            debtorAccount = normalizeAccount(debtorAccount);
        }
        validate(payload);

        StringBuilder sb = new StringBuilder();
        appendField(sb, "K", payload.identificationCode().name());
        appendField(sb, "V", VERSION);
        appendField(sb, "C", CHARSET);
        appendField(sb, "R", creditorAccount);
        appendField(sb, "N", payload.creditorName());
        appendField(sb, "I", formatAmount(payload.currency(), payload.amount()));
        appendOptional(sb, "O", debtorAccount);
        appendOptional(sb, "P", payload.debtorName());
        appendOptional(sb, "SF", payload.paymentCode());
        appendOptional(sb, "S", payload.paymentPurpose());

        if (!sb.isEmpty() && sb.charAt(sb.length() - 1) == DELIMITER) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }

    public static String normalizeAccount(String account) {
        String sanitized = sanitizeAccount(account);
        if (!ACCOUNT_PATTERN.matcher(sanitized).matches()) {
            throw new IllegalArgumentException(
                    "IQE_001: account must contain only digits, dashes, or whitespace, got: " + account);
        }
        if (sanitized.length() < BANK_CODE_LENGTH + 1 + CONTROL_NUMBER_LENGTH
                || sanitized.length() > NORMALIZED_ACCOUNT_LENGTH) {
            throw new IllegalArgumentException(
                    "IQE_002: account must contain bank code (3 digits), account number (1-13 digits), "
                            + "and control number (2 digits), got: " + account);
        }

        String bankCode = sanitized.substring(0, BANK_CODE_LENGTH);
        String accountNumber = sanitized.substring(BANK_CODE_LENGTH, sanitized.length() - CONTROL_NUMBER_LENGTH);
        String controlNumber = sanitized.substring(sanitized.length() - CONTROL_NUMBER_LENGTH);

        String normalized = bankCode + leftPadWithZeros(accountNumber) + controlNumber;
        validateControlDigits(normalized, account);
        return normalized;
    }

    private static String sanitizeAccount(String account) {
        if (account == null) {
            throw new NullPointerException("IQE_004: account is required");
        }
        return account.replaceAll("[-\\s]", "");
    }

    private static String leftPadWithZeros(String value) {
        return "0".repeat(IpsQrEncoder.ACCOUNT_NUMBER_LENGTH - value.length()) + value;
    }

    private static void validateControlDigits(String normalized, String original) {
        String accountWithoutControl = normalized.substring(0, NORMALIZED_ACCOUNT_LENGTH - CONTROL_NUMBER_LENGTH);
        int expectedControl = 98 - mod97(accountWithoutControl + "00");
        int actualControl = Integer.parseInt(normalized.substring(NORMALIZED_ACCOUNT_LENGTH - CONTROL_NUMBER_LENGTH));
        if (actualControl != expectedControl) {
            throw new IllegalArgumentException(
                    "IQE_003: account control digits are invalid, got: " + original);
        }
    }

    private static int mod97(String digits) {
        int remainder = 0;
        for (int i = 0; i < digits.length(); i++) {
            remainder = (remainder * 10 + digits.charAt(i) - '0') % 97;
        }
        return remainder;
    }

    private void validate(IpsQrPayload payload) {
        BigDecimal amount = payload.amount();
        if (amount.compareTo(AMOUNT_MIN) < 0 || amount.compareTo(AMOUNT_MAX) > 0) {
            throw new IllegalArgumentException(
                    "IQE_005: amount must be between " + AMOUNT_MIN + " and " + AMOUNT_MAX + ", got: " + amount);
        }

        String paymentCode = payload.paymentCode();
        if (paymentCode != null && !paymentCode.isBlank() && !PAYMENT_CODE_PATTERN.matcher(paymentCode).matches()) {
            throw new IllegalArgumentException(
                    "IQE_006: paymentCode (SF) must be exactly 3 digits, got: " + paymentCode);
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
