package com.revolut;

import com.google.gson.Gson;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.revolut.controller.ControllerModule;
import com.revolut.dao.DaoModule;
import com.revolut.service.ServiceModule;

public class MainModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new ControllerModule());
        install(new DaoModule());
        install(new ServiceModule());
        bind(Gson.class).in(Singleton.class);
        bind(Application.class).in(Singleton.class);
    }
}
