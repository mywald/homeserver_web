package delivery.switches;

import application.device.*;
import application.switches.*;
import com.google.inject.*;
import org.restlet.representation.*;
import org.restlet.resource.*;

public class SwitchStatusResource extends ServerResource {

    @Inject
    private RequestSwitchStatusInteractor interactor;

    @SuppressWarnings("UnusedDeclaration")
    @Get
    public Representation switchOn() {
        Integer address = Integer.valueOf((String) getRequestAttributes().get("address"));
        Integer channel = Integer.valueOf((String) getRequestAttributes().get("channel"));

        boolean on = interactor.statusOf(new Device(address), new Channel(channel));
        return new StringRepresentation("Switch: " + on);
    }

}