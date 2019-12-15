package com.revolut.controller;

import com.google.gson.Gson;
import com.revolut.controller.request.ChangeBalanceRequest;
import com.revolut.controller.request.CreateAcoountRequest;
import com.revolut.controller.response.AccountResponse;
import com.revolut.controller.response.ResponseMessage;
import com.revolut.exception.AccountNotFoundException;
import com.revolut.exception.WithdrawAccountException;
import com.revolut.model.Account;
import com.revolut.service.AccountService;
import spark.ExceptionHandler;
import spark.Route;

import javax.inject.Inject;
import java.math.BigDecimal;

import static spark.Spark.*;

public class AccountController implements BaseController {

    public static final String ACCOUNTS_PATH = "/accounts";
    public static final String WITHDRAW_ACCOUNT_PATH = "/withdrawAccount";
    public static final String TOP_UP_PATH = "/top-up";
    private static final String ACCOUNT_NUMBER = "accountNumber";

    private Gson gson;
    private AccountService accountService;


    @Inject
    AccountController(Gson gson, AccountService accountService) {
        this.accountService = accountService;
        this.gson = gson;
    }

    @Override
    public void init() {
        post(ACCOUNTS_PATH, ACCEPT_TYPE, createAccount, gson::toJson);
        get(ACCOUNTS_PATH + "/:" + ACCOUNT_NUMBER, ACCEPT_TYPE, getAccountByNumber, gson::toJson);
        put(ACCOUNTS_PATH + "/:" + ACCOUNT_NUMBER + WITHDRAW_ACCOUNT_PATH, ACCEPT_TYPE, withdrawAccount, gson::toJson);
        put(ACCOUNTS_PATH + "/:" + ACCOUNT_NUMBER + TOP_UP_PATH, ACCEPT_TYPE, topUpAccount, gson::toJson);
        exception(AccountNotFoundException.class, accountNotFoundExceptionHandler);
        exception(WithdrawAccountException.class, withdrawAccountExceptionHandler);
    }

    private Route createAccount = (request, response) -> {
        String body = request.body();
        CreateAcoountRequest createAcoountRequest = gson.fromJson(body, CreateAcoountRequest.class);
        Account account = accountService.createAccount(createAcoountRequest.getAccountHolder());
        return new ResponseMessage<>("New account was created.", true, AccountResponse.convert(account));
    };

    private Route withdrawAccount = ((request, response) -> {
        String accountNumber = request.params(ACCOUNT_NUMBER);
        ChangeBalanceRequest changeBalanceRequest = gson.fromJson(request.body(), ChangeBalanceRequest.class);
        accountService.withdrawAccount(accountNumber, new BigDecimal(changeBalanceRequest.getAmount()));
        return new ResponseMessage<>("Bank account was withdrawed", true, null);
    });

    private Route topUpAccount = (request, response) -> {
        String accountNumber = request.params(ACCOUNT_NUMBER);
        ChangeBalanceRequest changeBalanceRequest = gson.fromJson(request.body(), ChangeBalanceRequest.class);
        accountService.topUpAccount(accountNumber, new BigDecimal(changeBalanceRequest.getAmount()));
        return new ResponseMessage<>("Account was topped up.", true, null);
    };

    private Route getAccountByNumber = (request, response) -> {
        String accountNumber = request.params(ACCOUNT_NUMBER);
        Account account = accountService.findAccountByNumber(accountNumber);
        return new ResponseMessage<>("Account was found by account number", true, AccountResponse.convert(account));
    };

    private ExceptionHandler<AccountNotFoundException> accountNotFoundExceptionHandler = (exception, request, response) -> {
        response.status(404);
        ResponseMessage<Object> responseMessage = new ResponseMessage<>(exception.getMessage(), false, null);
        response.body(gson.toJson(responseMessage));
    };

    private ExceptionHandler<WithdrawAccountException> withdrawAccountExceptionHandler = (exception, request, response) -> {
        response.status(409);
        ResponseMessage<Object> responseMessage = new ResponseMessage<>(exception.getMessage(), false, null);
        response.body(gson.toJson(responseMessage));
    };
}
