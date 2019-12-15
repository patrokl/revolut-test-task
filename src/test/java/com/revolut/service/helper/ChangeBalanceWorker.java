package com.revolut.service.helper;

import com.revolut.service.AccountService;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;

/**
 * Helper class for concurrent balance changing.
 */
@Builder
public final class ChangeBalanceWorker implements Runnable {

    private AccountService accountService;
    private String acountNumber;
    private BigDecimal amount;
    private Action action;
    private int times;
    private CountDownLatch countDownLatch;

    @Override
    public void run() {
        try {
            for (int i = 0; i < times; i++) {
                if (action == Action.WITHDROW) {
                    accountService.withdrawAccount(acountNumber, amount);
                } else {
                    accountService.topUpAccount(acountNumber, amount);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            countDownLatch.countDown();
        }
    }

    public enum Action {
        WITHDROW, TOPUP
    }
}
