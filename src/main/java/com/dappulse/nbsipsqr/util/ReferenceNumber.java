package com.dappulse.nbsipsqr.util;

import java.util.regex.Pattern;

public class ReferenceNumber extends Control {

    public static final int V99 = 99;
    public static final int V97 = 97;
    public static final int V10 = 10;
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

    @Override
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
        return validateControlNumber(transformNumber, model);
    }

    @Override
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

    public String toNumber() {
        return number.isEmpty()
                ? EMPTY
                : String.format("%02d%s", model, number);
    }
}
