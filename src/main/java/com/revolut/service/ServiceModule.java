package com.revolut.service;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class ServiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(AccountService.class).in(Singleton.class);
        bind(TransferService.class).in(Singleton.class);
    }
}
