package com.revolut.controller;

import com.revolut.Main;
import com.revolut.controller.request.ChangeBalanceRequest;
import com.revolut.controller.request.CreateAcoountRequest;
import com.revolut.controller.response.AccountResponse;
import com.revolut.controller.response.ResponseMessage;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import spark.Spark;

@RunWith(JUnit4.class)
public class AccountControllerIntegrationTest {

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
    public void createAccount_simpleInvoke_shouldSucceed() throws Exception {
        // Arrange 
        String accountHolder = "Harry Potter";
        CreateAcoountRequest request = new CreateAcoountRequest(accountHolder);
        // Act
        ResponseMessage<AccountResponse> responseMessage =
                sparkApiTestHelper.makePostRequest(AccountController.ACCOUNTS_PATH, request, AccountResponse.class);
        // Assert
        AccountResponse expected = AccountResponse.builder()
                .accountNumber(responseMessage.getData().getAccountNumber())
                .accountHolder(accountHolder)
                .balance("0")
                .build();
        sparkApiTestHelper.assertResponseMessage(responseMessage, "New account was created.", true, expected);
    }

    @Test
    public void getAccountByNumber_simpleInvoke_shouldSucceed() throws Exception {
        // Arrange
        String accountHolder = "Harry Potter";
        CreateAcoountRequest request = new CreateAcoountRequest(accountHolder);
        ResponseMessage<AccountResponse> createResponse =
                sparkApiTestHelper.makePostRequest(AccountController.ACCOUNTS_PATH, request, AccountResponse.class);
        // Act
        String getByNumberPath = AccountController.ACCOUNTS_PATH + "/" + createResponse.getData().getAccountNumber();
        ResponseMessage<AccountResponse> getByNumberResponse = sparkApiTestHelper.makeGetRequest(getByNumberPath, AccountResponse.class);
        // Assert
        AccountResponse accountResponseExpected = AccountResponse.builder()
                .balance("0")
                .accountHolder(accountHolder)
                .accountNumber(createResponse.getData().getAccountNumber())
                .build();
        sparkApiTestHelper.assertResponseMessage(getByNumberResponse, "Account was found by account number", true, accountResponseExpected);
    }

    @Test
    public void getAccountByNumber_accountNotExist_shouldBeHandledException() throws Exception {
        // Arrange
        String accountHolder = "Harry Potter";
        CreateAcoountRequest request = new CreateAcoountRequest(accountHolder);
        ResponseMessage<AccountResponse> createResponse =
                sparkApiTestHelper.makePostRequest(AccountController.ACCOUNTS_PATH, request, AccountResponse.class);
        // Act
        String notExistedAccount = "1000";
        String getByNumberPath = AccountController.ACCOUNTS_PATH + "/" + notExistedAccount;
        ResponseMessage getByNumberResponse = sparkApiTestHelper.makeGetRequest(getByNumberPath, AccountResponse.class);
        //Assert
        sparkApiTestHelper.assertResponseMessage(
                getByNumberResponse,
                "Account doesn't exist. Account number: " + notExistedAccount,
                false,
                null
        );

    }


    @Test
    public void topUpAccount_simpleInvoke_shouldSucceed() throws Exception {
        // Arrange
        String accountHolder = "Harry Potter";
        CreateAcoountRequest request = new CreateAcoountRequest(accountHolder);
        ResponseMessage<AccountResponse> responseMessage =
                sparkApiTestHelper.makePostRequest(AccountController.ACCOUNTS_PATH, request, AccountResponse.class);
        // Act
        ChangeBalanceRequest changeBalanceRequest = new ChangeBalanceRequest("100");
        String topUpPath = AccountController.ACCOUNTS_PATH
                + "/"
                + responseMessage.getData().getAccountNumber()
                + AccountController.TOP_UP_PATH;
        ResponseMessage<Object> topUpResponse = sparkApiTestHelper.makePutRequest(
                topUpPath,
                changeBalanceRequest,
                Object.class);
        // Assert
        sparkApiTestHelper.assertResponseMessage(topUpResponse, "Account was topped up.", true, null);
    }

    @Test
    public void withdrawAccount_simpleInvoke_shouldSucceed() throws Exception {
        // Arrange
        String accountHolder = "Harry Potter";
        CreateAcoountRequest request = new CreateAcoountRequest(accountHolder);
        ResponseMessage<AccountResponse> responseMessage =
                sparkApiTestHelper.makePostRequest(AccountController.ACCOUNTS_PATH, request, AccountResponse.class);
        ChangeBalanceRequest changeBalanceRequest = new ChangeBalanceRequest("100");
        String topUpPath = AccountController.ACCOUNTS_PATH
                + "/"
                + responseMessage.getData().getAccountNumber()
                + AccountController.TOP_UP_PATH;
        sparkApiTestHelper.makePutRequest(topUpPath, changeBalanceRequest, Object.class);
        // Act
        String withdrawPath = AccountController.ACCOUNTS_PATH
                + "/"
                + responseMessage.getData().getAccountNumber()
                + AccountController.WITHDRAW_ACCOUNT_PATH;
        changeBalanceRequest = new ChangeBalanceRequest("99");
        ResponseMessage<Object> withdrawResponse = sparkApiTestHelper.makePutRequest(withdrawPath, changeBalanceRequest, Object.class);
        //Assert
        sparkApiTestHelper.assertResponseMessage(
                withdrawResponse,
                "Bank account was withdrawed",
                true,
                null
        );
    }

    @Test
    public void withdrawAccount_balanceLessThanWithdrawAmount_shouldBeHandledException() throws Exception {
        // Arrange
        String accountHolder = "Harry Potter";
        CreateAcoountRequest request = new CreateAcoountRequest(accountHolder);
        ResponseMessage<AccountResponse> responseMessage =
                sparkApiTestHelper.makePostRequest(AccountController.ACCOUNTS_PATH, request, AccountResponse.class);
        ChangeBalanceRequest changeBalanceRequest = new ChangeBalanceRequest("100");
        String topUpPath = AccountController.ACCOUNTS_PATH
                + "/"
                + responseMessage.getData().getAccountNumber()
                + AccountController.TOP_UP_PATH;
        sparkApiTestHelper.makePutRequest(topUpPath, changeBalanceRequest, Object.class);
        // Act
        String withdrawPath = AccountController.ACCOUNTS_PATH
                + "/"
                + responseMessage.getData().getAccountNumber()
                + AccountController.WITHDRAW_ACCOUNT_PATH;
        changeBalanceRequest = new ChangeBalanceRequest("101");
        ResponseMessage<Object> withdrawResponse = sparkApiTestHelper.makePutRequest(withdrawPath, changeBalanceRequest, Object.class);
        //Assert
        sparkApiTestHelper.assertResponseMessage(
                withdrawResponse,
                "Account can't be withdrawn. Balance is less than amount of withdrawing",
                false,
                null
        );
    }
}