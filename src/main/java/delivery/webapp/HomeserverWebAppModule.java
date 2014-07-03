package delivery.webapp;

import com.google.inject.*;
import org.restlet.*;

public class HomeserverWebAppModule extends AbstractModule {

    @Override
    protected void configure() {

        bind(Application.class).to(HomeserverWebApp.class);

    }
}
