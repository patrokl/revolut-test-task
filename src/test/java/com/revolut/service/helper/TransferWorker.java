package com.revolut.service.helper;

import com.revolut.service.TransferService;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;


/**
 * Helper class for concurrent transfer between accounts.
 */
@Builder
public final class TransferWorker implements Runnable {

    private TransferService transferService;
    private String remmiterAccountNumber;
    private String beneficiaryAccountNumber;
    private int times;
    private BigDecimal amount;
    private CountDownLatch countDownLatch;

    @Override
    public void run() {
        try {
            for (int i = 0; i < 1000; i++) {
                transferService.transfer(remmiterAccountNumber, beneficiaryAccountNumber, new BigDecimal(1));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            countDownLatch.countDown();
        }
    }
}
