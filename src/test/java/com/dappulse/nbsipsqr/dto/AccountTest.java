package com.dappulse.nbsipsqr.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class AccountTest {

    @ParameterizedTest
    @ValueSource(strings = { "", "1234" })
    void ofTEST_notGood(String value) {
        // GIVEN
        // WHEN THEN
        assertThrows(IllegalArgumentException.class, () -> {
            Account.of(value);
        });
    }

    @Test
    void ofTEST_good() {
        // GIVEN
        String accountValues = "123000012345678958";
        // WHEN
        Account actual = Account.of(accountValues);
        // THEN
        assertEquals("123000012345678958", actual.toString());
    }

    @Test
    void ofTEST_goodWithSpaces() {
        // GIVEN
        String accountValues = "123 0000123456789 58";
        // WHEN
        Account actual = Account.of(accountValues);
        // THEN
        assertEquals("123000012345678958", actual.toString());
    }

    @Test
    void ofTEST_goodWithDashes() {
        // GIVEN
        String accountValues = "123-0000123456789-58";
        // WHEN
        Account actual = Account.of(accountValues);
        // THEN
        assertEquals("123000012345678958", actual.toString());
    }

    @Test
    void ofTEST_goodWithLetters() {
        // GIVEN
        String accountValues = "Account: 123_0000123456789_58";
        // WHEN
        Account actual = Account.of(accountValues);
        // THEN
        assertEquals("123000012345678958", actual.toString());
    }

    @Test
    void ofTEST_missingOneZero() {
        // GIVEN
        String accountValues = "123 111123456789 58";
        // WHEN
        Account actual = Account.of(accountValues);
        // THEN
        assertEquals("123011112345678958", actual.toString());
    }

    @Test
    void ofTEST_missingFourZeroes() {
        // GIVEN
        String accountValues = "123 123456789 58";
        // WHEN
        Account actual = Account.of(accountValues);
        // THEN
        assertEquals("123000012345678958", actual.toString());
    }

    @Test
    void ofTEST_missingNineZeroes() {
        // GIVEN
        String accountValues = "123 1234 58";
        // WHEN
        Account actual = Account.of(accountValues);
        // THEN
        assertEquals("123000000000123458", actual.toString());
    }

    @Test
    void ofTEST_missingTwelveZeroes() {
        // GIVEN
        String accountValues = "123 1 58";
        // WHEN
        Account actual = Account.of(accountValues);
        // THEN
        assertEquals("123000000000000158", actual.toString());
    }

    @Test
    void controllNumberTEST_58() {
        // GIVEN
        Account account = Account.of("123 123456789 58");
        // WHEN
        String actual = account.controllNumber();
        // THEN
        assertEquals("58", actual);
    }

    @Test
    void isValidTEST_isValid() {
        // GIVEN
        String accountValues = "123 8643 36";
        Account account = Account.of(accountValues);
        // WHEN
        boolean actual = account.isValid();
        // THEN
        assertTrue(actual);
    }

}
