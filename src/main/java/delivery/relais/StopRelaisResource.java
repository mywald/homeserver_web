package delivery.relais;

import com.google.inject.*;
import hardware.*;
import org.restlet.representation.*;
import org.restlet.resource.*;

public class StopRelaisResource extends ServerResource {

    private Rfm12bTransceiver rfm12b;

    @Inject
    public StopRelaisResource(Rfm12bTransceiver rfm12b) {
        this.rfm12b = rfm12b;
    }


    @SuppressWarnings("UnusedDeclaration")
    @Get("json")
    public Representation startRelais() {
        rfm12b.sendpackage(0x77);
        return new StringRepresentation("started");
    }

}