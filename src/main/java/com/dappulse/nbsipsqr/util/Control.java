package com.dappulse.nbsipsqr.util;

import java.math.BigInteger;

public abstract class Control {

    public static final String EMPTY = "";
    public static final int V2 = 2;
    public static final int V0 = 0;

    public abstract String transformNumber();

    public abstract Validation validate();

    public Validation validateControlNumber(String transformed, int value) {
        if (value == V0) {
            return Validation.of();
        }
        BigInteger number = new BigInteger(transformed.substring(V2));
        BigInteger controlNumber = new BigInteger(transformed.substring(V0, V2));
        BigInteger model = BigInteger.valueOf((long) value);
        BigInteger boudary = model.add(BigInteger.ONE);
        BigInteger calcControlNumber = boudary.subtract(number.mod(model));
        boolean match = calcControlNumber.compareTo(controlNumber) == V0;
        return match
                ? Validation.of()
                : Validation.ofError(String.format("Control number not match given: %s calculated: %s",
                        controlNumber.toString(), calcControlNumber.toString()));
    }

    public record Validation(String message) {
        public static Validation of() {
            return new Validation(EMPTY);
        }

        public static Validation ofError(String message) {
            return new Validation(message);
        }

        public boolean hasError() {
            return !message.isEmpty();
        }
    }
}
