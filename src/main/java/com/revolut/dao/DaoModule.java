package com.revolut.dao;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class DaoModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(AccountDao.class).in(Singleton.class);
        bind(TransferDao.class).in(Singleton.class);
    }

}
