package com.revolut.controller;

import com.revolut.controller.request.ChangeBalanceRequest;
import com.revolut.controller.request.CreateAcoountRequest;
import com.revolut.controller.response.AccountDto;
import com.revolut.controller.response.ResponseMessage;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class AccountControllerIntegrationTest {

    @Rule
    public SparkApiTestRule sparkApiTestRule = new SparkApiTestRule(8080);

    @Test
    public void createAccount_simpleInvoke_shouldSucceed() throws Exception {
        // Arrange 
        String accountHolder = "Harry Potter";
        CreateAcoountRequest request = new CreateAcoountRequest(accountHolder);
        // Act
        ResponseMessage<AccountDto> responseMessage =
                sparkApiTestRule.makePostRequest(AccountController.ACCOUNTS_PATH, request, AccountDto.class);
        // Assert
        AccountDto expected = AccountDto.builder()
                .accountNumber("1")
                .accountHolder(accountHolder)
                .balance("0")
                .build();
        sparkApiTestRule.assertResponseMessage(responseMessage, "New account was created.", true, expected);
    }

    @Test
    public void getAccountByNumber_simpleInvoke_shouldSucceed() throws Exception {
        // Arrange
        String accountHolder = "Harry Potter";
        CreateAcoountRequest request = new CreateAcoountRequest(accountHolder);
        ResponseMessage<AccountDto> createResponse =
                sparkApiTestRule.makePostRequest(AccountController.ACCOUNTS_PATH, request, AccountDto.class);
        // Act
        String getByNumberPath = AccountController.ACCOUNTS_PATH + "/" + createResponse.getData().getAccountNumber();
        ResponseMessage<AccountDto> getByNumberResponse = sparkApiTestRule.makeGetRequest(getByNumberPath, AccountDto.class);
        // Assert
        AccountDto accountDtoExpected = AccountDto.builder()
                .balance("0")
                .accountHolder(accountHolder)
                .accountNumber("1")
                .build();
        sparkApiTestRule.assertResponseMessage(getByNumberResponse, "Account was found by account number", true, accountDtoExpected);
    }

    @Test
    public void getAccountByNumber_accountNotExist_shouldBeHandledException() throws Exception {
        // Arrange
        String accountHolder = "Harry Potter";
        CreateAcoountRequest request = new CreateAcoountRequest(accountHolder);
        ResponseMessage<AccountDto> createResponse =
                sparkApiTestRule.makePostRequest(AccountController.ACCOUNTS_PATH, request, AccountDto.class);
        // Act
        String notExistedAccount = "1000";
        String getByNumberPath = AccountController.ACCOUNTS_PATH + "/" + notExistedAccount;
        ResponseMessage getByNumberResponse = sparkApiTestRule.makeGetRequest(getByNumberPath, AccountDto.class);
        //Assert
        sparkApiTestRule.assertResponseMessage(
                getByNumberResponse,
                "Account doesn't exist. Account number: " + notExistedAccount,
                false,
                Object.class
        );

    }


    @Test
    public void topUpAccount_simpleInvoke_shouldSucceed() throws Exception {
        // Arrange
        String accountHolder = "Harry Potter";
        CreateAcoountRequest request = new CreateAcoountRequest(accountHolder);
        ResponseMessage<AccountDto> responseMessage =
                sparkApiTestRule.makePostRequest(AccountController.ACCOUNTS_PATH, request, AccountDto.class);
        // Act
        ChangeBalanceRequest changeBalanceRequest = new ChangeBalanceRequest("100");
        String topUpPath = AccountController.ACCOUNTS_PATH
                + "/"
                + responseMessage.getData().getAccountNumber()
                + AccountController.TOP_UP_PATH;
        ResponseMessage<Object> topUpResponse = sparkApiTestRule.makePutRequest(
                topUpPath,
                changeBalanceRequest,
                Object.class);
        // Assert
        sparkApiTestRule.assertResponseMessage(topUpResponse, "Account was topped up.", true, null);
    }

    @Test
    public void withdrawAccount_simpleInvoke_shouldSucceed() throws Exception {
        // Arrange
        String accountHolder = "Harry Potter";
        CreateAcoountRequest request = new CreateAcoountRequest(accountHolder);
        ResponseMessage<AccountDto> responseMessage =
                sparkApiTestRule.makePostRequest(AccountController.ACCOUNTS_PATH, request, AccountDto.class);
        ChangeBalanceRequest changeBalanceRequest = new ChangeBalanceRequest("100");
        String topUpPath = AccountController.ACCOUNTS_PATH
                + "/"
                + responseMessage.getData().getAccountNumber()
                + AccountController.TOP_UP_PATH;
        sparkApiTestRule.makePutRequest(topUpPath, changeBalanceRequest, Object.class);
        // Act
        String withdrawPath = AccountController.ACCOUNTS_PATH
                + "/"
                + responseMessage.getData().getAccountNumber()
                + AccountController.WITHDRAW_ACCOUNT_PATH;
        changeBalanceRequest = new ChangeBalanceRequest("99");
        ResponseMessage<Object> withdrawResponse = sparkApiTestRule.makePutRequest(withdrawPath, changeBalanceRequest, Object.class);
        //Assert
        sparkApiTestRule.assertResponseMessage(
                withdrawResponse,
                "Bank account wath withdrawed",
                true,
                null
        );
    }

    @Test
    public void withdrawAccount_balanceLessThanWithdrawAmount_shouldBeHandledException() throws Exception {
        // Arrange
        String accountHolder = "Harry Potter";
        CreateAcoountRequest request = new CreateAcoountRequest(accountHolder);
        ResponseMessage<AccountDto> responseMessage =
                sparkApiTestRule.makePostRequest(AccountController.ACCOUNTS_PATH, request, AccountDto.class);
        ChangeBalanceRequest changeBalanceRequest = new ChangeBalanceRequest("100");
        String topUpPath = AccountController.ACCOUNTS_PATH
                + "/"
                + responseMessage.getData().getAccountNumber()
                + AccountController.TOP_UP_PATH;
        sparkApiTestRule.makePutRequest(topUpPath, changeBalanceRequest, Object.class);
        // Act
        String withdrawPath = AccountController.ACCOUNTS_PATH
                + "/"
                + responseMessage.getData().getAccountNumber()
                + AccountController.WITHDRAW_ACCOUNT_PATH;
        changeBalanceRequest = new ChangeBalanceRequest("101");
        ResponseMessage<Object> withdrawResponse = sparkApiTestRule.makePutRequest(withdrawPath, changeBalanceRequest, Object.class);
        //Assert
        sparkApiTestRule.assertResponseMessage(
                withdrawResponse,
                "Account can't be withdrawn. Balance is less than amount of withdrawing",
                false,
                null
        );
    }
}