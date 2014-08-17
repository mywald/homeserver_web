package hardware;

import com.google.inject.*;
import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.*;
import com.pi4j.wiringpi.*;

import java.nio.*;
import java.util.logging.*;

@Singleton
public class GpioHeader {
    private static final Logger LOG = Logger.getLogger(GpioHeader.class.getCanonicalName());

    private GpioController gpio;
    private GpioPinDigitalInput nIRQ;

    @Inject
    public GpioHeader() {
        gpio = GpioFactory.getInstance();
        nIRQ = gpio.provisionDigitalInputPin(RaspiPin.GPIO_05, "nIRQ of RFM12B", PinPullResistance.OFF);

        int fd = Spi.wiringPiSPISetup(0, 1000000);
        if (fd <= -1) {
            throw new RuntimeException("SPI SETUP FAILED");
        }
    }

    public int spi(int data, String msg) {
        LOG.finest("Sending Data to SPI (" + msg + "): " + data);
        byte[] bytes = ByteBuffer.allocate(2).putShort((short) data).array();

        Spi.wiringPiSPIDataRW(0, bytes, 2);

        return ByteBuffer.wrap(bytes).getShort();
    }


    public boolean nIRQisHigh() {
        return nIRQ.isHigh();
    }


    public void shutdown() {
        gpio.shutdown();
    }

    public void setnIRQListener(GpioPinListener listener) {
        if (listener == null){
            gpio.removeAllListeners();
        } else {
            gpio.addListener(listener, nIRQ);
        }
    }
}
