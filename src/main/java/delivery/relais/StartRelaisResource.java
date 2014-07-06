package delivery.relais;

import com.google.inject.*;
import hardware.*;
import org.restlet.representation.*;
import org.restlet.resource.*;

public class StartRelaisResource extends ServerResource {

    private Rfm12bTransceiver rfm12b;

    @Inject
    public StartRelaisResource(Rfm12bTransceiver rfm12b) {
        this.rfm12b = rfm12b;
    }


    @SuppressWarnings("UnusedDeclaration")
    @Get("json")
    public Representation startRelais() {
        rfm12b.sendpackage(0x99);
        return new StringRepresentation("started");
    }

}