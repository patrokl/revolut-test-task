package com.revolut.service.helper;

import com.revolut.service.AccountService;
import lombok.Builder;

import java.util.concurrent.CountDownLatch;

/**
 * Helper class for concurrent account creation.
 */
@Builder
public final class CreateAccountWorker implements Runnable {
    private AccountService accountService;
    private String holderName;
    private int times;
    private CountDownLatch countDownLatch;

    @Override
    public void run() {
        for (int i = 0; i < times; i++) {
            accountService.createAccount(holderName + i);
        }
        countDownLatch.countDown();
    }
}
