package com.revolut.controller;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

public class ControllerModule extends AbstractModule {

    @Override
    protected void configure() {
        Multibinder<BaseController> controllerBinder = Multibinder.newSetBinder(binder(), BaseController.class);
        controllerBinder.addBinding().to(AccountController.class);
        controllerBinder.addBinding().to(TransferController.class);
    }
}
