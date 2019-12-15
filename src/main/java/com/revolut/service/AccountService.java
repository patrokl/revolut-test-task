package com.revolut.service;

import com.revolut.dao.AccountDao;
import com.revolut.exception.AccountNotFoundException;
import com.revolut.exception.WithdrawAccountException;
import com.revolut.model.Account;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Service for working with {@link Account}.
 */
public class AccountService {

    private final AtomicLong accountNumberGenerator = new AtomicLong(0);
    private final AccountDao accountDao;

    @Inject
    AccountService(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    /**
     * Creates {@link Account} with passed account holder name.
     *
     * @param accountHolder account holder name.
     * @return created {@link Account}
     */
    public Account createAccount(String accountHolder) {
        long accountNumber = accountNumberGenerator.incrementAndGet();
        Account account = new Account(String.valueOf(accountNumber), accountHolder);
        accountDao.save(account);
        return account;
    }

    /**
     * Withdraws {@code amount} from account with account number {@code accountNumber}.
     *
     * @throws WithdrawAccountException if withdraw amount is less than account balance
     * @throws AccountNotFoundException if account doesn't exist
     */
    public void withdrawAccount(String accountNumber, BigDecimal amount) throws AccountNotFoundException, WithdrawAccountException {
        Account account = findAccountByNumber(accountNumber);
        account.withdrawAmount(amount);
    }

    /**
     * Top ups account on {@code amount} with account number {@code accountNumber}.
     *
     * @throws AccountNotFoundException if account doesn't exist
     */
    public void topUpAccount(String accountNumber, BigDecimal amount) throws AccountNotFoundException {
        Account account = findAccountByNumber(accountNumber);
        account.topUp(amount);
    }

    /**
     * Finds account by {@code accountNumber}.
     *
     * @throws AccountNotFoundException if account doesn't exist
     */
    public Account findAccountByNumber(String accountNumber) throws AccountNotFoundException {
        Account account = accountDao.findByAccountNumber(accountNumber);
        if (account == null) {
            throw new AccountNotFoundException("Account doesn't exist. Account number: " + accountNumber);
        }
        return account;
    }

    /**
     * Gets all accounts.
     */
    public Collection<Account> getAllAccounts() {
        return accountDao.getAll();
    }
}
