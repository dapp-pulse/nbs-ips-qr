package com.dappulse.nbsipsqr;

public class AccountUtil {

    public static final int BANK_CODE_LEN = 3;
    public static final int ACCOUNT_NUMBER_LEN = 13;
    public static final int CONTROL_NUMBER_LEN = 2;
    public static final int ACCOUNT_SIZE = BANK_CODE_LEN + ACCOUNT_NUMBER_LEN + CONTROL_NUMBER_LEN;
    public static final String ZERO = "0";

    public record Account(String bankCode, String accountNumber, String controlNumber) {
        public static Account of(StringBuilder sb) {
            return Account.of(sb.substring(0, BANK_CODE_LEN),
                    sb.substring(BANK_CODE_LEN, BANK_CODE_LEN + ACCOUNT_NUMBER_LEN),
                    sb.substring(BANK_CODE_LEN + ACCOUNT_NUMBER_LEN, ACCOUNT_SIZE));
        }

        public static Account of(String bankCode, String accountNumber, String controlNumber) {
            return new Account(bankCode, accountNumber, controlNumber);
        }
    }

    public static Account parseAccount(String accountString) {
        StringBuilder sbDigits = new StringBuilder(ACCOUNT_SIZE);
        StringBuilder sb = new StringBuilder(ACCOUNT_SIZE);
        // skip non digit
        for (char element : accountString.toCharArray()) {
            if (Character.isDigit(element)) {
                sbDigits.append(element);
            }
        }

        return Account.of(sb);
    }

}
