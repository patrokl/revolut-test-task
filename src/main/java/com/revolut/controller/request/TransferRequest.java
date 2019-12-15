package com.revolut.controller.request;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransferRequest {

    private final String beneficiaryAccountNumber;

    private final String remitterAccountNumber;

    private final String amount;
}
