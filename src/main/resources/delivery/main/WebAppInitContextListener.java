package delivery.main;

import com.google.inject.*;
import delivery.infrastructure.*;

public class WebAppInitContextListener extends com.google.inject.servlet.GuiceServletContextListener {

    @Override
    protected Injector getInjector() {
        return Guice.createInjector(new ServletModule(), new HomeserverWebAppModule());
    }

}
