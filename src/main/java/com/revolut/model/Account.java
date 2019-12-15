package com.revolut.model;

import com.revolut.exception.WithdrawAccountException;

import java.math.BigDecimal;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class Account {

    private transient ReadWriteLock accountLock;

    private final String accountNumber;

    private final String accountHolder;

    private BigDecimal balance;

    public Account(String accountNumber, String accountHolder) {
        balance = new BigDecimal(0);
        this.accountHolder = accountHolder;
        this.accountNumber = accountNumber;
        accountLock = new ReentrantReadWriteLock();
    }

    public ReadWriteLock getAccountLock() {
        return accountLock;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountHolder() {
        return accountHolder;
    }


    public BigDecimal getBalance() {
        accountLock.readLock().lock();
        try {
            return balance;
        } finally {
            accountLock.readLock().unlock();
        }
    }

    public void topUp(BigDecimal amount) {
        accountLock.writeLock().lock();
        try {
            balance = balance.add(amount);
        } finally {
            accountLock.writeLock().unlock();
        }
    }

    public void withdrawAmount(BigDecimal amount) throws WithdrawAccountException {
        accountLock.writeLock().lock();
        try {
            if (balance.compareTo(amount) < 0) {
                throw new WithdrawAccountException("Account can't be withdrawn. Balance is less than amount of " +
                        "withdrawing");
            }
            balance = balance.subtract(amount);
        } finally {
            accountLock.writeLock().unlock();
        }
    }
}
