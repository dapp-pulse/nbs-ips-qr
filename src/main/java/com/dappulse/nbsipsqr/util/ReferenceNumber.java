package com.dappulse.nbsipsqr.util;

import java.math.BigInteger;
import java.util.regex.Pattern;

public class ReferenceNumber {

    public static final int V99 = 99;
    public static final int V97 = 97;
    public static final int V10 = 10;
    public static final int V2 = 2;
    public static final int V0 = 0;
    public static final String EMPTY = "";
    public static final String DASH = "-";
    public static final String SPACE = " ";
    public static final char A = 'A';
    public static final Pattern PATTERN_NUMBERS_LETTERS_SPACES = Pattern.compile("[A-Z0-9 -]{1,23}");
    public static final Pattern PATTERN_NUMBERS_LETTERS = Pattern.compile("[A-Z0-9]{1,23}");

    private final int model;
    private final String number;

    private ReferenceNumber(Integer model, String number) {
        this.model = parseModel(model);
        this.number = parseNumber(number);
    }

    public static ReferenceNumber of(Integer model, String number) {
        return new ReferenceNumber(model, number);
    }

    public Integer parseModel(Integer model) {
        return model == null
                ? V0
                : model;
    }

    public String parseNumber(String number) {
        return number == null || number.isBlank()
                ? EMPTY
                : number;
    }

    public Validation validate() {
        if (model < V0 || model > V99) {
            return Validation.ofError("Model must be in [0,99]");
        }
        if (model == V0 && number.isEmpty()) {
            return Validation.of();
        }
        boolean matches = model == V97
                ? PATTERN_NUMBERS_LETTERS.matcher(number)
                                         .matches()
                : PATTERN_NUMBERS_LETTERS_SPACES.matcher(number)
                                                .matches();
        if (!matches) {
            return Validation.ofError("Number is not in boundary [A-Z0-9] size 23");
        }
        String transformNumber = transformNumber();
        return validateControlNumber(transformNumber);
    }

    public String transformNumber() {
        StringBuilder sb = new StringBuilder();
        for (char ch : number.toCharArray()) {
            if (Character.isDigit(ch)) {
                sb.append(ch);
            }
            if (Character.isUpperCase(ch)) {
                int value = ch - A + V10;
                sb.append(value);
            }
        }
        sb.append(V0)
          .append(V0);
        return sb.toString();
    }

    public Validation validateControlNumber(String transformed) {
        if (this.model == V0) {
            return Validation.of();
        }
        BigInteger number = new BigInteger(transformed.substring(V2));
        BigInteger controlNumber = new BigInteger(transformed.substring(V0, V2));
        BigInteger model = BigInteger.valueOf((long) this.model);
        BigInteger boudary = model.add(BigInteger.ONE);
        BigInteger calcControlNumber = boudary.subtract(number.mod(model));
        boolean match = calcControlNumber.compareTo(controlNumber) == V0;
        return match
                ? Validation.of()
                : Validation.ofError(String.format("Control number not match given: %s calculated: %s",
                        controlNumber.toString(), calcControlNumber.toString()));
    }

    public String toNumber() {
        String replaced = number.replaceAll(SPACE, DASH);
        return number.isEmpty()
                ? EMPTY
                : String.format("%02d%s", model, replaced);
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
