package delivery.infrastructure;

import com.google.inject.*;
import org.restlet.*;
import org.restlet.resource.*;
import org.restlet.routing.*;

public class GuiceRouter extends Router {
    private final Injector injector;

    public GuiceRouter(Injector injector, Context context) {
        super(context);
        this.injector = injector;
    }

    @Override
    public Finder createFinder(Class targetClass) {
        return new GuiceFinder(injector, getContext(), targetClass);
    }


}
