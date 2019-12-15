package com.revolut.service;

import com.revolut.dao.AccountDao;
import com.revolut.exception.AccountNotFoundException;
import com.revolut.exception.WithdrawAccountException;
import com.revolut.model.Account;
import com.revolut.service.helper.ChangeBalanceWorker;
import com.revolut.service.helper.CreateAccountWorker;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class AccountServiceTest {

    private static final String TEST_ACCOUNT_HOLDER_NAME = "Harry Potter";
    private AccountService accountService;

    @Before
    public void setUp() {
        AccountDao accountDao = new AccountDao();
        accountService = new AccountService(accountDao);
    }

    @Test
    public void createAccount_10000accounts10Threads_shouldCreateAllAccounts() throws Exception {
        //Arrange
        CountDownLatch countDownLatch = new CountDownLatch(10);
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        //Act
        for (int i = 0; i < 10; i++) {
            CreateAccountWorker createAccountWorker = CreateAccountWorker.builder()
                    .accountService(accountService)
                    .holderName(i + "_")
                    .times(1000)
                    .countDownLatch(countDownLatch)
                    .build();
            executorService.execute(createAccountWorker);
        }
        countDownLatch.await();
        //Assert
        Collection<String> holdersActual = accountService.getAllAccounts()
                .stream()
                .map(Account::getAccountHolder)
                .sorted()
                .collect(Collectors.toList());
        List<String> holdersExpected = generateExpectedHolderNames();
        assertEquals(holdersExpected, holdersActual);
    }

    private List<String> generateExpectedHolderNames() {
        List<String> holdersExpected = new ArrayList<>();
        int holderPrefix = 0;
        int holderSuffix = 0;
        for (int i = 0; i < 10000; i++) {
            holdersExpected.add(holderPrefix + "_" + holderSuffix);
            holderSuffix += 1;
            if (holderSuffix == 1000) {
                holderSuffix = 0;
                holderPrefix += 1;

            }
        }
        Collections.sort(holdersExpected);
        return holdersExpected;
    }


    @Test
    public void withdrawAccount_10000timesOneThread_balanceShouldBe0() throws Exception {
        //Arrange
        String accountNumber = accountService.createAccount(TEST_ACCOUNT_HOLDER_NAME).getAccountNumber();
        accountService.topUpAccount(accountNumber, new BigDecimal(10_000));
        //Act
        BigDecimal amount = new BigDecimal(1);
        for (int i = 0; i < 10_000; i++) {
            accountService.withdrawAccount(accountNumber, amount);
        }
        //Assert
        Account account = accountService.findAccountByNumber(accountNumber);
        assertEquals(new BigDecimal(0), account.getBalance());
    }

    @Test
    public void withdrawAccount_10000times10Threads_balanceShouldBe0() throws Exception {
        //Arrange
        String accountNumber = accountService.createAccount(TEST_ACCOUNT_HOLDER_NAME).getAccountNumber();
        accountService.topUpAccount(accountNumber, new BigDecimal(10_000));
        ChangeBalanceWorker.ChangeBalanceWorkerBuilder changeBalancerBuilder = ChangeBalanceWorker.builder()
                .accountService(accountService)
                .acountNumber(accountNumber)
                .amount(new BigDecimal(1))
                .action(ChangeBalanceWorker.Action.WITHDROW)
                .times(1000);
        //Act
        changeBalance(changeBalancerBuilder, 10);
        //Assert
        Account account = accountService.findAccountByNumber(accountNumber);
        assertEquals(new BigDecimal(0), account.getBalance());
    }

    @Test(expected = WithdrawAccountException.class)
    public void withdrawAccount_balanceLessThanAmount_shouldThrowException() throws Exception {
        Account account = accountService.createAccount(TEST_ACCOUNT_HOLDER_NAME);
        accountService.topUpAccount(account.getAccountNumber(), new BigDecimal(100));
        accountService.withdrawAccount(account.getAccountNumber(), new BigDecimal(101));
    }

    @Test
    public void topUpAccount_10000timesOneThread_balanceShouldBe10_000() throws Exception {
        //Arrange
        String accountNumber = accountService.createAccount(TEST_ACCOUNT_HOLDER_NAME).getAccountNumber();
        BigDecimal amount = new BigDecimal(1);
        //Act
        for (int i = 0; i < 10_000; i++) {
            accountService.topUpAccount(accountNumber, amount);
        }
        //Assert
        Account account = accountService.findAccountByNumber(accountNumber);
        assertEquals(new BigDecimal(10_000), account.getBalance());
    }

    @Test
    public void topUpAccount_10000times10Threads_balanceShouldBe10_000() throws Exception {
        //Arrange
        String accountNumber = accountService.createAccount(TEST_ACCOUNT_HOLDER_NAME).getAccountNumber();
        ChangeBalanceWorker.ChangeBalanceWorkerBuilder changeBalancerBuilder = ChangeBalanceWorker.builder()
                .accountService(accountService)
                .acountNumber(accountNumber)
                .amount(new BigDecimal(1))
                .action(ChangeBalanceWorker.Action.TOPUP)
                .times(1000);
        //Act
        changeBalance(changeBalancerBuilder, 10);
        //Assert
        Account account = accountService.findAccountByNumber(accountNumber);
        assertEquals(new BigDecimal(10_000), account.getBalance());
    }

    @Test(expected = AccountNotFoundException.class)
    public void findAccountByNumber_numberNotExist_shouldThrowException() throws Exception {
        // Account number: 1
        accountService.createAccount(TEST_ACCOUNT_HOLDER_NAME);
        // Account number: 2
        accountService.createAccount(TEST_ACCOUNT_HOLDER_NAME);
        // Account number: 3
        accountService.createAccount(TEST_ACCOUNT_HOLDER_NAME);

        accountService.findAccountByNumber("100");
    }

    private void changeBalance(ChangeBalanceWorker.ChangeBalanceWorkerBuilder changeBalanceWorkerBuilder, int threadNumber)
            throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(threadNumber);
        ExecutorService executorService = Executors.newFixedThreadPool(threadNumber);
        changeBalanceWorkerBuilder.countDownLatch(countDownLatch);
        for (int i = 0; i < threadNumber; i++) {
            executorService.execute(changeBalanceWorkerBuilder.build());
        }
        countDownLatch.await();
    }


}