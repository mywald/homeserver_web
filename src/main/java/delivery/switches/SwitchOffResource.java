package delivery.switches;

import application.device.*;
import application.switches.*;
import com.google.inject.*;
import org.restlet.representation.*;
import org.restlet.resource.*;

public class SwitchOffResource extends ServerResource {

    @Inject
    private SwitchOffInteractor interactor;

    @SuppressWarnings("UnusedDeclaration")
    @Get
    public Representation switchOn() {
        Integer address = Integer.valueOf((String) getRequestAttributes().get("address"));
        Integer channel = Integer.valueOf((String) getRequestAttributes().get("channel"));

        interactor.switchOff(new Device(address), new Channel(channel));
        return new StringRepresentation("Switch OFF sent");
    }

}