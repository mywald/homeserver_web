package delivery.helloworld;

import com.google.inject.*;
import org.restlet.representation.*;
import org.restlet.resource.*;

public class HelloWorldResource extends ServerResource {


    @Inject
    public HelloWorldResource() {
    }


    @SuppressWarnings("UnusedDeclaration")
    @Get("json")
    public Representation userProfile() {
            return new StringRepresentation("hello kitty");
    }

}