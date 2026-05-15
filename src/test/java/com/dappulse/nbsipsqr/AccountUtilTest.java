package com.dappulse.nbsipsqr;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.dappulse.nbsipsqr.AccountUtil.Account;

class AccountUtilTest {

    @Test
    void parseAccountTEST_allGood() {
        // GIVEN
        String accountString = "123 0000000456789 58";
        // WHEN
        Account actual = AccountUtil.parseAccount(accountString);
        // THEN
        assertEquals(Account.of("123", "0000000456789", "58"), actual);
    }

}
