package com.dappulse.nbsipsqr.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ReferenceNumberTest {

    @Test
    void ofTEST_modelIsNull() {
        // GIVEN
        Integer model = null;
        String number = "1234-1234";
        ReferenceNumber referenceNumber = ReferenceNumber.of(model, number);
        // WHEN
        String actual = referenceNumber.toNumber();
        // THEN
        assertEquals("001234-1234", actual);
    }

    @Test
    void ofTEST_modelIs0numberWithSpace() {
        // GIVEN
        Integer model = null;
        String number = "1234 1234";
        ReferenceNumber referenceNumber = ReferenceNumber.of(model, number);
        // WHEN
        String actual = referenceNumber.toNumber();
        // THEN
        assertEquals("001234-1234", actual);
    }

    @Test
    void ofTEST_modelIsZero() {
        // GIVEN
        Integer model = 0;
        String number = "1234";
        ReferenceNumber referenceNumber = ReferenceNumber.of(model, number);
        // WHEN
        String actual = referenceNumber.toNumber();
        // THEN
        assertEquals("001234", actual);
    }

    @Test
    void ofTEST_modelIsZeroNumberNull() {
        // GIVEN
        Integer model = 0;
        String number = null;
        ReferenceNumber referenceNumber = ReferenceNumber.of(model, number);
        // WHEN
        String actual = referenceNumber.toNumber();
        // THEN
        assertEquals("", actual);
    }

    @ParameterizedTest
    @ValueSource(strings = { "", " ", "\t" })
    void ofTEST_modelIsZero(String data) {
        // GIVEN
        Integer model = 0;
        String number = data;
        ReferenceNumber referenceNumber = ReferenceNumber.of(model, number);
        // WHEN
        String actual = referenceNumber.toNumber();
        // THEN
        assertEquals("", actual);
    }

    @ParameterizedTest
    @ValueSource(ints = { -2, -1, 100, 101 })
    void ofTEST_modelIs(int data) {
        // GIVEN
        Integer model = data;
        String number = "1234";
        ReferenceNumber referenceNumber = ReferenceNumber.of(model, number);
        // WHEN
        boolean actual = referenceNumber.validate()
                                        .hasError();
        // THEN
        assertTrue(actual);
    }

    @ParameterizedTest
    @ValueSource(strings = { "", "0013!456", "00A12a34", "0013 456", "00A12-34", "123456789123456789123456" })
    void ofTEST_numberIsBadModelIs97(String data) {
        // GIVEN
        Integer model = 97;
        String number = data;
        ReferenceNumber referenceNumber = ReferenceNumber.of(model, number);
        // WHEN
        boolean actual = referenceNumber.validate()
                                        .hasError();
        // THEN
        assertTrue(actual);
    }

    @ParameterizedTest
    @ValueSource(strings = { "", "0013!A456", "00A12a34", "123456789123456789123456" })
    void ofTEST_numberIsBadModelIs11(String data) {
        // GIVEN
        Integer model = 11;
        String number = data;
        ReferenceNumber referenceNumber = ReferenceNumber.of(model, number);
        // WHEN
        boolean actual = referenceNumber.validate()
                                        .hasError();
        // THEN
        assertTrue(actual);
    }

    @ParameterizedTest
    @ValueSource(strings = { "", "A", "0013A456", "00A1-2Z34", "00A1-2Z34-45", "00A1 2Z34 45",
            "12345678912345678912345" })
    void ofTEST_numberIsGoodModelIs0(String data) {
        // GIVEN
        Integer model = 0;
        String number = data;
        ReferenceNumber referenceNumber = ReferenceNumber.of(model, number);
        // WHEN
        boolean actual = referenceNumber.validate()
                                        .hasError();
        // THEN
        assertFalse(actual);
    }

    @ParameterizedTest
    @ValueSource(strings = { "4313A456", "02A12Z34", "63345678912345678912345" })
    void ofTEST_numberIsGoodModelIs97(String data) {
        // GIVEN
        Integer model = 97;
        String number = data;
        ReferenceNumber referenceNumber = ReferenceNumber.of(model, number);
        // WHEN
        boolean actual = referenceNumber.validate()
                                        .hasError();
        // THEN
        assertFalse(actual);
    }

    @Test
    void ofTEST_modelIs11NumberWithLetters() {
        // GIVEN
        Integer model = 11;
        String number = "123AB123";
        ReferenceNumber referenceNumber = ReferenceNumber.of(model, number);
        // WHEN
        String actual = referenceNumber.transformNumber();
        // THEN
        assertEquals("123101112300", actual);
    }

    @Test
    void ofTEST_modelIs11NumberWithLettersAndSpaces() {
        // GIVEN
        Integer model = 11;
        String number = "123K L123";
        ReferenceNumber referenceNumber = ReferenceNumber.of(model, number);
        // WHEN
        String actual = referenceNumber.transformNumber();
        // THEN
        assertEquals("123202112300", actual);
    }

    @Test
    void ofTEST_modelIs11NumberWithLettersAndDashes() {
        // GIVEN
        Integer model = 11;
        String number = "12-3Y-Z1-23";
        ReferenceNumber referenceNumber = ReferenceNumber.of(model, number);
        // WHEN
        String actual = referenceNumber.transformNumber();
        // THEN
        assertEquals("123343512300", actual);
    }

    @ParameterizedTest
    @ValueSource(strings = { "54567812154820012", "14123412", "86A1" })
    void ofTEST_validateControllNumber(String data) {
        // GIVEN
        Integer model = 97;
        String number = data;
        ReferenceNumber referenceNumber = ReferenceNumber.of(model, number);
        String transformed = referenceNumber.transformNumber();
        // WHEN
        boolean actual = referenceNumber.validateControlNumber(transformed, model)
                                        .hasError();
        // THEN
        assertFalse(actual);
    }

}
