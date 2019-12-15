package com.revolut.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.revolut.controller.response.ResponseMessage;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;

import static org.junit.Assert.assertEquals;

class SparkApiTestHelper {

    private static final String CONTENT_TYPE = "content-type";
    private static final String APPLICATION_JSON = "application/json";
    private Gson gson = new Gson();
    private int port;
    private String urlPattern = "http://localhost:%d%s";

    SparkApiTestHelper(int port) {
        this.port = port;
    }

    <T> void assertResponseMessage(ResponseMessage<T> responseMessage, String message, boolean succeed, T expectedDataObject) {
        assertEquals(message, responseMessage.getMessage());
        assertEquals(succeed, responseMessage.isSucceed());
        assertEquals(expectedDataObject, responseMessage.getData());
    }

    <T> ResponseMessage<T> makeGetRequest(String path, Class<T> responseClass) {
        HttpGet request = new HttpGet(String.format(urlPattern, port, path));
        return makeRequest(request, responseClass);
    }

    <T> ResponseMessage<T> makePostRequest(String path, Object body, Class<T> responseClass) throws UnsupportedEncodingException {
        HttpPost request = new HttpPost(String.format(urlPattern, port, path));
        return makeRequestWithBody(request, body, responseClass);

    }

    <T> ResponseMessage<T> makePutRequest(String path, Object body, Class<T> responseClass) throws UnsupportedEncodingException {
        HttpPut request = new HttpPut(String.format(urlPattern, port, path));
        return makeRequestWithBody(request, body, responseClass);
    }

    private <T> ResponseMessage<T> makeRequestWithBody(HttpEntityEnclosingRequestBase request, Object body, Class<T> responseClass)
            throws UnsupportedEncodingException {
        StringEntity params = new StringEntity(gson.toJson(body));
        request.addHeader(CONTENT_TYPE, APPLICATION_JSON);
        request.setEntity(params);
        return makeRequest(request, responseClass);
    }

    private <T> ResponseMessage<T> makeRequest(HttpRequestBase request, Class<T> responseClass) {
        ResponseMessage<T> responseMessage = null;
        Type type = TypeToken.getParameterized(ResponseMessage.class, responseClass).getType();
        try (CloseableHttpClient httpClient = HttpClients.custom()
                .build(); CloseableHttpResponse response = httpClient.execute(request)) {
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));
            responseMessage = gson.fromJson(bufferedReader, type);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseMessage;
    }


}
