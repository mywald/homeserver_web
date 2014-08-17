package application.device;

import com.google.inject.*;
import hardware.*;

import java.util.*;


public class DeviceComm {

    private static final int TIMEOUT = 10000;

    @Inject
    private Rfm12bTransceiver rfm12b;


    public void send(Device device, Channel channel, Integer command) {
        rfm12b.send(device.asInt(), channel.asInt(), command);
    }

    public List<Integer> receive() {
        //TODO: remove checksum, or any other redundancy
        return rfm12b.waitForAnswer(TIMEOUT);
    }
}
