package com.revolut.controller;

import com.revolut.Main;
import com.revolut.controller.request.ChangeBalanceRequest;
import com.revolut.controller.request.CreateAcoountRequest;
import com.revolut.controller.request.TransferRequest;
import com.revolut.controller.response.AccountResponse;
import com.revolut.controller.response.ResponseMessage;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import spark.Spark;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class TransferControllerIntegrationTest {

    private SparkApiTestHelper sparkApiTestHelper = new SparkApiTestHelper(8080);

    @BeforeClass
    public static void startServer() {
        Main.main(null);
    }

    @AfterClass
    public static void stopServer() {
        Spark.stop();
    }

    @Test
    public void transfer_betweenTwoAccounts_shouldTransfer99() throws Exception {
        // Arrange
        ResponseMessage<AccountResponse> responseMessage1 = sparkApiTestHelper.makePostRequest(
                AccountController.ACCOUNTS_PATH,
                new CreateAcoountRequest("Harry Potter"),
                AccountResponse.class);
        ResponseMessage<AccountResponse> responseMessage2 = sparkApiTestHelper.makePostRequest(
                AccountController.ACCOUNTS_PATH,
                new CreateAcoountRequest("Tom Hardy"),
                AccountResponse.class);
        String topUpPath = AccountController.ACCOUNTS_PATH
                + "/"
                + responseMessage1.getData().getAccountNumber()
                + AccountController.TOP_UP_PATH;
        sparkApiTestHelper.makePutRequest(
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
        sparkApiTestHelper.makePostRequest(TransferController.TRANSFERS_PATH, transferRequest, Object.class);
        //Assert
        ResponseMessage<AccountResponse> beneficiaryAccountResponseMessage = sparkApiTestHelper.makeGetRequest(
                AccountController.ACCOUNTS_PATH + "/" + responseMessage2.getData().getAccountNumber(), AccountResponse.class);
        assertEquals(transferAmount, beneficiaryAccountResponseMessage.getData().getBalance());
    }


}