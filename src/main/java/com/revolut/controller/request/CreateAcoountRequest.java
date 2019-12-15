package com.revolut.controller.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateAcoountRequest {
    private String accountHolder;
}
