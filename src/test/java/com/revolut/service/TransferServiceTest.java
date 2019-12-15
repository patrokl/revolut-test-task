package com.revolut.service;

import com.revolut.dao.AccountDao;
import com.revolut.dao.TransferDao;
import com.revolut.model.Account;
import com.revolut.service.helper.TransferWorker;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class TransferServiceTest {

    private AccountService accountService;
    private TransferService transferService;

    @Before
    public void setUp() {
        AccountDao accountDao = new AccountDao();
        accountService = new AccountService(accountDao);
        TransferDao transferDao = new TransferDao();
        transferService = new TransferService(transferDao, accountService);
    }

    @Test
    public void transfer_twoThreads2000transfers_balanceShouldStateSame() throws Exception {
        BigDecimal balance = new BigDecimal(1000);

        Account account1 = accountService.createAccount("Account1");
        accountService.topUpAccount(account1.getAccountNumber(), balance);

        Account account2 = accountService.createAccount("Account2");
        accountService.topUpAccount(account2.getAccountNumber(), balance);

        CountDownLatch countDownLatch = new CountDownLatch(2);
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        TransferWorker worker1 = TransferWorker.builder()
                .transferService(transferService)
                .remmiterAccountNumber(account1.getAccountNumber())
                .beneficiaryAccountNumber(account2.getAccountNumber())
                .times(1000)
                .amount(new BigDecimal(1))
                .countDownLatch(countDownLatch)
                .build();
        TransferWorker worker2 = TransferWorker.builder()
                .transferService(transferService)
                .remmiterAccountNumber(account2.getAccountNumber())
                .beneficiaryAccountNumber(account1.getAccountNumber())
                .times(1000)
                .amount(new BigDecimal(1))
                .countDownLatch(countDownLatch)
                .build();
        executorService.execute(worker1);
        executorService.execute(worker2);

        countDownLatch.await();

        assertEquals(balance, account1.getBalance());
        assertEquals(balance, account2.getBalance());
        assertEquals(2000, transferService.getAllTransfers().size());
    }
}