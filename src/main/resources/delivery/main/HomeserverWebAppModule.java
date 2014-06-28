package delivery.main;

import com.google.inject.*;
import org.restlet.*;

class HomeserverWebAppModule extends AbstractModule {

    @Override
    protected void configure() {

        bind(Application.class).to(HomeserverWebApp.class);

    }
}
