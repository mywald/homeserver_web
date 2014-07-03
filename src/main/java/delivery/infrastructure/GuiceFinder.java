package delivery.infrastructure;

import com.google.inject.*;
import org.restlet.*;
import org.restlet.resource.*;

class GuiceFinder extends Finder {
    private final Injector injector;

    public GuiceFinder(Injector injector, Context context, Class<? extends ServerResource> targetClass) {
        super(context, targetClass);
        this.injector = injector;
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public ServerResource create(Class targetClass, Request request, Response response) {
        ServerResource res = (ServerResource) injector.getInstance(targetClass);
        return res;
    }
}