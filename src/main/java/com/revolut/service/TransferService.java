package com.revolut.service;

import com.revolut.dao.TransferDao;
import com.revolut.exception.AccountNotFoundException;
import com.revolut.exception.WithdrawAccountException;
import com.revolut.model.Account;
import com.revolut.model.Transfer;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Service for working with {@link Transfer}.
 */
public class TransferService {

    private final AtomicLong idGenerator = new AtomicLong(0);

    private final TransferDao transferDao;
    private final AccountService accountService;

    @Inject
    TransferService(TransferDao transferDao, AccountService accountService) {
        this.transferDao = transferDao;
        this.accountService = accountService;
    }

    /**
     * Transfers {@code amount} from account with number {@code remmiterAccountNumber} to account with number
     * {@code beneficiaryAccountNumber}.
     *
     * @throws WithdrawAccountException if withdraw amount is less than account balance
     * @throws AccountNotFoundException if one of account doesn't exist
     */
    public void transfer(String remmiterAccountNumber, String beneficiaryAccountNumber, BigDecimal amount) throws WithdrawAccountException, AccountNotFoundException {
        Account remmiterAccount = accountService.findAccountByNumber(remmiterAccountNumber);
        Account beneficiaryAccount = accountService.findAccountByNumber(beneficiaryAccountNumber);
        remmiterAccount.withdrawAmount(amount);
        beneficiaryAccount.topUp(amount);
        Transfer transfer = new Transfer(beneficiaryAccountNumber, remmiterAccountNumber, LocalDateTime.now(ZoneOffset.UTC), amount);
        transferDao.save(idGenerator.incrementAndGet(), transfer);
    }

    /**
     * Gets all transfers
     */
    public Collection<Transfer> getAllTransfers() {
        return transferDao.findAll();
    }


}
