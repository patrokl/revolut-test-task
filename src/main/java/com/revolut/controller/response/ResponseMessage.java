package com.revolut.controller.response;

import lombok.Value;

@Value
public class ResponseMessage<T> {

    String message;
    boolean succeed;
    T data;

}
