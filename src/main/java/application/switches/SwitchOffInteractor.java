package application.switches;

import application.device.*;
import com.google.inject.*;

public class SwitchOffInteractor {
    @Inject private DeviceComm comm;


    public void switchOff(Device device, Channel channel) {
        comm.send(device, channel, Commands.OFF);
    }
}
