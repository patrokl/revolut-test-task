package com.revolut.controller;

import com.google.gson.Gson;
import com.revolut.controller.request.TransferRequest;
import com.revolut.controller.response.ResponseMessage;
import com.revolut.dao.TransferDao;
import com.revolut.model.Transfer;
import com.revolut.service.TransferService;
import spark.Route;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Collection;

import static spark.Spark.get;
import static spark.Spark.post;

public class TransferController implements BaseController {

    public static final String TRANSFERS_PATH = "/transfers";
    private final TransferService transferService;
    private final TransferDao transferDao;
    private final Gson gson;

    @Inject
    TransferController(TransferService transferService, TransferDao transferDao, Gson gson) {
        this.transferService = transferService;
        this.gson = gson;
        this.transferDao = transferDao;
    }

    @Override
    public void init() {
        get(TRANSFERS_PATH, ACCEPT_TYPE, getAllTransfers(), gson::toJson);
        post(TRANSFERS_PATH, ACCEPT_TYPE, transfer(), gson::toJson);
    }

    private Route getAllTransfers() {
        return (request, response) -> {
            Collection<Transfer> all = transferDao.findAll();
            return new ResponseMessage<>("Transfers history.", true, all);
        };
    }

    private Route transfer() {
        return (request, response) -> {
            String body = request.body();
            TransferRequest transferRequest = gson.fromJson(body, TransferRequest.class);
            transferService.transfer(transferRequest.getRemitterAccountNumber(),
                    transferRequest.getBeneficiaryAccountNumber(), new BigDecimal(transferRequest.getAmount()));
            return new ResponseMessage<>(
                    String.format("Transfer from account %s to %s with amount %s was successful.",
                            transferRequest.getRemitterAccountNumber(),
                            transferRequest.getBeneficiaryAccountNumber(),
                            transferRequest.getAmount()),
                    true,
                    null);
        };
    }
}
