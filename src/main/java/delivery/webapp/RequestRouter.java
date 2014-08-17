package delivery.webapp;

import com.google.inject.*;
import delivery.switches.*;
import delivery.infrastructure.*;
import org.restlet.*;
import org.restlet.routing.*;

class RequestRouter extends GuiceRouter {

    public RequestRouter(Injector injector, Context context) {
        super(injector, context);

        Router router = new GuiceRouter(injector, getContext());
        attach("/public", router, Template.MODE_STARTS_WITH);

        router.attach("/switch/{address}/{channel}/on", SwitchOnResource.class);
        router.attach("/switch/{address}/{channel}/off", SwitchOffResource.class);
        router.attach("/switch/{address}/{channel}/status", SwitchStatusResource.class);
    }

}