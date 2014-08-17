package application.switches;

import application.device.*;
import com.google.inject.*;

public class SwitchOnInteractor {
    @Inject private DeviceComm comm;


    public void switchOn(Device device, Channel channel) {
        comm.send(device, channel, Commands.ON);
    }
}
