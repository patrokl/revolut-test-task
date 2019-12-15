package com.revolut;

import com.revolut.controller.BaseController;

import javax.inject.Inject;
import java.util.Set;

import static spark.Spark.port;

public class Application {

    private Set<BaseController> controllers;

    @Inject
    Application(Set<BaseController> controllers) {
        this.controllers = controllers;
    }

    /**
     * Sets Spark port and initialize all controllers.
     */
    void run() {
        port(8080);
        controllers.forEach(BaseController::init);
    }
}
