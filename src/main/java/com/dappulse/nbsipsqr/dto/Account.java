package com.dappulse.nbsipsqr.dto;

import java.util.Arrays;

public final class Account {

    public static final int BANK_CODE_SIZE = 3;
    public static final int ACCOUNT_NUMBER_SIZE = 13;
    public static final int CONTROL_NUMBER_SIZE = 2;
    public static final int SIZE = BANK_CODE_SIZE + ACCOUNT_NUMBER_SIZE + CONTROL_NUMBER_SIZE;
    public static final char ZERO = '0';
    public static final char EMPTY = ' ';
    public static final long V97 = 97L;

    private final char[] data;

    private Account() {
        data = new char[SIZE];
        Arrays.fill(data, EMPTY);
    }

    private Account(char[] values) {
        this();
        readDigits(values);
        validateSize();
        extendZeros();
    }

    public String bankCode() {
        return new String(copy(0, BANK_CODE_SIZE - 1));
    }

    public String accountNumber() {
        return new String(copy(BANK_CODE_SIZE - 1, BANK_CODE_SIZE + ACCOUNT_NUMBER_SIZE));
    }

    public String controllNumber() {
        return new String(copy(BANK_CODE_SIZE + ACCOUNT_NUMBER_SIZE, SIZE - 1));
    }

    public static Account of(String input) {
        Account account = new Account(input.toCharArray());
        return account;
    }

    public boolean isValid() {
        long value = readValidationValue();
        long cnum = value % V97;
        long controlNumber = Long.parseLong(controllNumber());
        return cnum == controlNumber;
    }

    private void readDigits(char[] values) {
        for (int i = 0, j = 0; i < values.length; i++) {
            if (Character.isDigit(values[i])) {
                data[j++] = values[i];
            }
        }
    }

    private void validateSize() {
        int size = SIZE - countEmptys();
        if (size < BANK_CODE_SIZE + CONTROL_NUMBER_SIZE + 1) {
            throw new IllegalArgumentException("Provided data size is not valid size: " + size);
        }
    }

    private void extendZeros() {
        int empties = countEmptys();
        if (empties == 0) {
            return;
        }
        int accountAndControllCount = SIZE - empties - BANK_CODE_SIZE;
        for (int i = SIZE - empties - 1, j = 0, step = empties; j < accountAndControllCount; j++, i--) {
            data[i + step] = data[i];
            data[i] = ZERO;
        }
        for (int i = BANK_CODE_SIZE + accountAndControllCount; i < SIZE - accountAndControllCount; i++) {
            data[i] = ZERO;
        }
    }

    private long readValidationValue() {
        char[] copyData = copy(0, SIZE - 1);
        copyData[SIZE - 1] = ZERO;
        copyData[SIZE - 2] = ZERO;
        return Long.parseLong(new String(copyData));
    }

    private char[] copy(int startIndex, int endIndex) {
        char[] out = new char[endIndex - startIndex + 1];
        for (int i = startIndex, j = 0; i <= endIndex; i++, j++) {
            out[j] = data[i];
        }
        return out;
    }

    private int countEmptys() {
        int empties = 0;
        for (int i = SIZE - 1; i >= 0; i--) {
            if (data[i] == EMPTY) {
                empties++;
            }
        }
        return empties;
    }

    @Override
    public String toString() {
        return new String(data);
    }
}
