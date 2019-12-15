package com.revolut.controller;

import com.revolut.controller.request.ChangeBalanceRequest;
import com.revolut.controller.request.CreateAcoountRequest;
import com.revolut.controller.request.TransferRequest;
import com.revolut.controller.response.AccountDto;
import com.revolut.controller.response.ResponseMessage;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class TransferControllerIntegrationTest {

    @Rule
    public SparkApiTestRule sparkApiTestRule = new SparkApiTestRule(8080);

    @Test
    public void transfer_betweenTwoAccounts_shouldTransfer99() throws Exception {
        // Arrange
        ResponseMessage<AccountDto> responseMessage1 = sparkApiTestRule.makePostRequest(
                AccountController.ACCOUNTS_PATH,
                new CreateAcoountRequest("Harry Potter"),
                AccountDto.class);
        ResponseMessage<AccountDto> responseMessage2 = sparkApiTestRule.makePostRequest(
                AccountController.ACCOUNTS_PATH,
                new CreateAcoountRequest("Tom Hardy"),
                AccountDto.class);
        String topUpPath = AccountController.ACCOUNTS_PATH
                + "/"
                + responseMessage1.getData().getAccountNumber()
                + AccountController.TOP_UP_PATH;
        sparkApiTestRule.makePutRequest(
                topUpPath,
                new ChangeBalanceRequest("100"),
                Object.class);
        String transferAmount = "99";
        TransferRequest transferRequest = TransferRequest.builder()
                .beneficiaryAccountNumber(responseMessage2.getData().getAccountNumber())
                .remitterAccountNumber(responseMessage1.getData().getAccountNumber())
                .amount(transferAmount)
                .build();
        //Act
        sparkApiTestRule.makePostRequest(TransferController.TRANSFERS_PATH, transferRequest, Object.class);
        //Assert
        ResponseMessage<AccountDto> beneficiaryAccountResponseMessage = sparkApiTestRule.makeGetRequest(
                AccountController.ACCOUNTS_PATH + "/" + responseMessage2.getData().getAccountNumber(), AccountDto.class);
        assertEquals(transferAmount, beneficiaryAccountResponseMessage.getData().getBalance());
    }


}