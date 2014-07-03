package delivery.webapp;

import com.google.inject.*;
import org.restlet.*;

import java.util.*;

class HomeserverWebApp extends Application {

    @Inject
    private Injector injector;

    public HomeserverWebApp() {
            System.out.println("starting");
    }

    @Override
    public synchronized void start() throws Exception {
        super.start();
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    @Override
    public Restlet createInboundRoot() {
        return new RequestRouter(injector, getContext());
    }

}
