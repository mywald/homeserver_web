package delivery.main;

import com.google.inject.*;
import delivery.helloworld.*;
import delivery.infrastructure.*;
import org.restlet.*;
import org.restlet.routing.*;

class RequestRouter extends GuiceRouter {


    public RequestRouter(Injector injector, Context context) {
        super(injector, context);

        Router router = new GuiceRouter(injector, getContext());
        attach("/public", router, Template.MODE_STARTS_WITH);

        router.attach("/hello/world", HelloWorldResource.class);
    }

}