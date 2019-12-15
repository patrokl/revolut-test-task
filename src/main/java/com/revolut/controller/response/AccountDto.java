package com.revolut.controller.response;

import com.revolut.model.Account;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountDto {

    private String accountNumber;

    private String accountHolder;

    private String balance;

    public static AccountDto convert(Account account) {
        if (account == null) {
            return AccountDto.builder().build();
        }
        return AccountDto.builder()
                .accountHolder(account.getAccountHolder())
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance().toPlainString())
                .build();
    }
}
