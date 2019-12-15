package com.revolut.controller.response;

import com.revolut.model.Account;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountResponse {

    private String accountNumber;

    private String accountHolder;

    private String balance;

    public static AccountResponse convert(Account account) {
        if (account == null) {
            return AccountResponse.builder().build();
        }
        return AccountResponse.builder()
                .accountHolder(account.getAccountHolder())
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance().toPlainString())
                .build();
    }
}
