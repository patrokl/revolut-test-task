package com.revolut.dao;

import com.revolut.model.Account;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class AccountDao {

    private final Map<String, Account> accountStorage = new ConcurrentHashMap<>();

    public Account findByAccountNumber(String accountNumber) {
        return accountStorage.get(accountNumber);
    }

    public void save(Account account) {
        accountStorage.put(account.getAccountNumber(), account);
    }

    public Collection<Account> getAll() {
        return new ArrayList<>(accountStorage.values());
    }

}
