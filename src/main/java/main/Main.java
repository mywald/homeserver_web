package main;

import com.google.inject.*;
import delivery.infrastructure.*;
import delivery.webapp.*;
import hardware.*;

public class Main extends com.google.inject.servlet.GuiceServletContextListener {

    @Override
    protected Injector getInjector() {
        return Guice.createInjector(new ServletModule(), new HomeserverWebAppModule(), new RaspiHardwareModule());
    }

}
