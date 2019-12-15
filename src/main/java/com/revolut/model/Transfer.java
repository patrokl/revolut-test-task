package com.revolut.model;


import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transfer {


    private String beneficiaryAccountNumber;

    private String remitterAccountNumber;

    private LocalDateTime transferDateTime;

    private BigDecimal amount;

    public Transfer(String beneficiaryAccountNumber, String remitterAccountNumber, LocalDateTime transferDateTime, BigDecimal amount) {
        this.beneficiaryAccountNumber = beneficiaryAccountNumber;
        this.remitterAccountNumber = remitterAccountNumber;
        this.transferDateTime = transferDateTime;
        this.amount = amount;
    }
}
