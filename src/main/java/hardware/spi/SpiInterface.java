package hardware.spi;

import com.google.inject.*;
import com.pi4j.io.gpio.*;
import com.pi4j.wiringpi.*;
import org.apache.commons.io.*;

import java.io.*;
import java.lang.reflect.*;
import java.nio.*;
import java.util.logging.*;

@Singleton
public class SpiInterface {
    private static final Logger LOG = Logger.getLogger(SpiInterface.class.getCanonicalName());

    private GpioController gpio;
    private GpioPinDigitalInput nIRQ;

    public SpiInterface() {
        try {
            FileUtils.cleanDirectory(new File(System.getProperty("java.io.tmpdir")));
        } catch (Exception e) {
            LOG.log(Level.INFO, "Could not clean lib folder", e);
        }

        try {
            System.setProperty("java.library.path", System.getProperty("java.io.tmpdir"));

            Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
            fieldSysPath.setAccessible(true);
            fieldSysPath.set(null, null);
        } catch (Exception e) {
            LOG.log(Level.INFO, "Could not init lib folder", e);
        }

        gpio = GpioFactory.getInstance();
        nIRQ = gpio.provisionDigitalInputPin(RaspiPin.GPIO_05, "nIRQ of RFM12B", PinPullResistance.OFF);

        int fd = Spi.wiringPiSPISetup(0, 1000000);
        if (fd <= -1) {
            throw new RuntimeException("SPI SETUP FAILED");
        }

        //TODO: cshigh = false
    }

    public void sendpackage() {
        initRfm12b();
        sendDataToAddress(0x23, 0x99);
    }

    private void initRfm12b() {
        send(0x0000, "Read status register");
        send(0x80D8, "Enable send and receive Registers, 433MHz, 12.5pF");
        send(0x8208, "enable xtal");
        send(0xA640, "Frequency");
        send(0xC6A0, "Bitrate");
        //Empfaengersteuerung
        send(0x94C0, "VDI, Fast, 67kHz, 0dBm,-103dBm");
        send(0xC2AC, "Data Filter & Clock Recovery");
        send(0xCC76, "Taktgenerator");
        //Sendersteuerung
        send(0xCA83, "FIFO8, SYNC");
        send(0xCED4, "Synch Pattern");
        send(0xC487, "Auto Frequency Control");
        send(0x9820, "!mp,Frequenzhub=45khz, MAX OUT");
        send(0xE000, "Wakeup Timer not used");
        send(0xC800, "Low Duty cycle Not use");
        send(0xC000, "1.0MHz, 2.2V");

        sleep(50);

        send(0x0000, "Read Status register");
    }

    private boolean isReadyToSend() {
        return nIRQ.isHigh();
    }

    private void sendDataToAddress(int address, int data) {
        while (!isReadyToSend()) {
            sleep(50);
        }

        send(0x0000, "Read Status register");
        send(0x8008, "Disable FIFO Register");
        send(0x80D8, "Enable FIFO Register");

        send(0x8238, "enable transmitter, enable xtal, enable PLL synthesizer");

        sendDataByte(0xAA, "PREAMBLE senden");
        sendDataByte(0xAA, "PREAMBLE senden");
        sendDataByte(0xAA, "PREAMBLE senden");
        sendDataByte(0x2D, "HI Byte senden");
        sendDataByte(0xD4, "LOW Byte for Frame-Detection senden");

        sendDataByte(address, "Adresse als Nutzdaten-Byte senden");
        sendDataByte(address, "Adresse als Nutzdaten-Byte senden");
        sendDataByte(address, "Adresse als Nutzdaten-Byte senden");
        sendDataByte(data, "Nutzdaten Byte senden");
        sendDataByte(data, "Nutzdaten Byte senden");
        sendDataByte(data, "Nutzdaten Byte senden");

        sendDataByte(0xAA, "PREAMBLE senden");
        sendDataByte(0xAA, "PREAMBLE senden");

        send(0x8208, "turn off transmitter");
        send(0x0000, "Read Status register");
    }

    private void sendDataByte(int data, String text) {
        boolean sendRegisterExpectsByte = false;
        while (!sendRegisterExpectsByte) {
            int stat = send(0x0000, "Read Status register");
            sendRegisterExpectsByte = (stat & 0b1000000000000000) > 0;
        }
        send(0xB800 | data, text);
    }

    private int send(int data, String msg) {
        LOG.finest("Sending Data to SPI (" + msg + "): " + data);
        byte[] bytes = ByteBuffer.allocate(2).putShort((short) data).array();

        Spi.wiringPiSPIDataRW(0, bytes, 2);

        return ByteBuffer.wrap(bytes).getShort();
    }

    private void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

}
