package delivery.helloworld;

import com.google.inject.*;
import hardware.spi.*;
import org.restlet.representation.*;
import org.restlet.resource.*;

public class HelloWorldResource extends ServerResource {

    private SpiInterface spiInterface;

    @Inject
    public HelloWorldResource(SpiInterface spiInterface) {
        this.spiInterface = spiInterface;
    }


    @SuppressWarnings("UnusedDeclaration")
    @Get("json")
    public Representation userProfile() {
        spiInterface.sendpackage();
        return new StringRepresentation("hello kitty");
    }

}