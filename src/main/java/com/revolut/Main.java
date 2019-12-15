package com.revolut;

import com.google.inject.Guice;

public class Main {

    /**
     * Create {@link Guice} injector with all project dependencies and runs {@link Application}.
     *
     * @param args not used
     */
    public static void main(String[] args) {
        Application application = Guice.createInjector(new MainModule()).getInstance(Application.class);
        application.run();
    }
}
