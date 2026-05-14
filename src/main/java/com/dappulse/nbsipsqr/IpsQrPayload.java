package com.dappulse.nbsipsqr;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Typed model for an NBS IPS QR payload.
 * Tag names from the NBS IPS QR specification are noted on each field.
 * Mandatory fields are enforced at construction time.
 */
public record IpsQrPayload(
        IdentificationCode identificationCode, // K — mandatory
        String creditorAccount,                // R — mandatory, payee bank account number
        String creditorName,                   // N — mandatory, payee name and location (e.g. "Acme d.o.o., Beograd")
        String currency,                       // I prefix — mandatory, e.g. "RSD"
        BigDecimal amount,                     // I value — mandatory
        String debtorAccount,                  // O — optional, payer bank account number
        String debtorName,                     // P — optional, payer name and address
        String paymentCode,                    // SF — optional, 3-digit payment code (šifra plaćanja)
        String paymentPurpose                  // S — optional, payment purpose text (svrha plaćanja)
) {

    /**
     * NBS IPS QR identification code (tag K).
     * Declares the context in which the QR code is presented.
     */
    public enum IdentificationCode {
        PR, // printed invoice / payment slip
        PT, // QR displayed by merchant at POS
        PK, // QR displayed by customer at POS
        EK  // e-commerce
    }

    public IpsQrPayload {
        Objects.requireNonNull(identificationCode, "identificationCode (K) is required");
        Objects.requireNonNull(creditorAccount, "creditorAccount (R) is required");
        Objects.requireNonNull(creditorName, "creditorName (N) is required");
        Objects.requireNonNull(currency, "currency (I) is required");
        Objects.requireNonNull(amount, "amount (I) is required");
    }
}
