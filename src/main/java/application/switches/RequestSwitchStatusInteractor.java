package application.switches;

import application.device.*;
import com.google.inject.*;
import hardware.*;

import java.util.*;

public class RequestSwitchStatusInteractor {

    @Inject private DeviceComm comm;

    public boolean statusOf(Device device, Channel channel) {
        comm.send(device, channel, Commands.REQUEST_STATUS);
        try {
            return parseAnswer(comm.receive());
        } catch (Rfm12bTimeoutException ex) {
            throw new StatusUnknownException();
        }
    }

    private boolean parseAnswer(List<Integer> answer) {
        if (answer.get(2) == Commands.ON) {
            return true;
        } else if (answer.get(2) == Commands.OFF) {
            return false;
        }
        throw new StatusUnknownException();
   }

    private class StatusUnknownException extends RuntimeException {
    }
}
