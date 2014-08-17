package hardware;

import application.device.*;
import com.google.inject.*;
import com.pi4j.io.gpio.event.*;

import java.util.*;
import java.util.logging.*;

import static hardware.HardwareUtils.*;

@Singleton
public class Rfm12bTransceiver  implements GpioPinListenerDigital {
    private static final Logger LOG = Logger.getLogger(Rfm12bTransceiver.class.getCanonicalName());
    private static final long SEND_TIMEOUT = 1000;


    private enum TransceiveMode {
        SENDER,
        RECEIVER
    }

    private GpioHeader gpio;
    private TransceiveMode txmode;
    private List<Integer> txbuffer = new ArrayList<>(16);
    private List<Integer> receivedData;

    @Inject
    public Rfm12bTransceiver(GpioHeader gpio) {
        this.gpio = gpio;

        initRfm12bCommon();
        initRfm12bAsReceiver();
    }

    public void send(Integer... data) {
        initRfm12bAsSender();

        long startTime = System.currentTimeMillis();

        while (!isReadyToSend()) {
            sleep(50);
            if ((System.currentTimeMillis() > (startTime + SEND_TIMEOUT))){
                throw new Rfm12bTimeoutException(startTime, SEND_TIMEOUT);
            }
        }

        gpio.spi(0x0000, "Read Status register");
        gpio.spi(0x8008, "Disable FIFO Register");
        gpio.spi(0x80D8, "Enable FIFO Register");

        gpio.spi(0x8238, "enable transmitter, enable xtal, enable PLL synthesizer");

        sendDataByte(0xAA, "PREAMBLE senden");
        sendDataByte(0xAA, "PREAMBLE senden");
        sendDataByte(0xAA, "PREAMBLE senden");
        sendDataByte(0x2D, "HI Byte senden");
        sendDataByte(0xD4, "LOW Byte for Frame-Detection senden");

        for (Integer dataByte : data) {
            sendDataByte(dataByte, "Send data");
        }

        sendDataByte(Commands.END_OF_STREAM, "EOS");
        sendDataByte(Commands.END_OF_STREAM, "EOS");
        sendDataByte(0xAA, "PREAMBLE senden");
        sendDataByte(0xAA, "PREAMBLE senden");

        gpio.spi(0x8208, "turn off transmitter");
        gpio.spi(0x0000, "Read Status register");

        System.out.println("Data sent: " + Arrays.asList(data));

        sleep(100);

        initRfm12bAsReceiver();
    }

    public List<Integer> waitForAnswer(int timeoutMillis) {
        long startTime = System.currentTimeMillis();
        receivedData = null;
        while (receivedData == null) {
            HardwareUtils.sleep(50);
            if (System.currentTimeMillis() > (startTime + timeoutMillis)){
                throw new Rfm12bTimeoutException(startTime, timeoutMillis);
            }
        }
        return receivedData;
    }

    //private class IncomingDataListener implements GpioPinListenerDigital {
        @Override
        public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
            System.out.println("IRQ pin state changed");
            if (gpio.nIRQisHigh() && TransceiveMode.RECEIVER.equals(txmode)) {
                System.out.println("Incoming Data Buffer full");

                int status = gpio.spi(0x0000, "Read status register");
                System.out.println("Status is:" + status);
                boolean bufferIsFull = (status & 0b1000000000000000) > 0;

                if (bufferIsFull) {
                    dataReceived();
                }
            }
        }
    //}

    private void dataReceived() {
        int data = gpio.spi(0xB000, "Read data register") & 0x00FF;

        System.out.println("Data is " + data);

        if (data == Commands.END_OF_STREAM) {
            receivedData = new ArrayList<>(txbuffer);
            txbuffer.clear();

            //TODO: when not waiting for explicit answer, throw an data received event
        } else {
            txbuffer.add(data);
        }
    }


    private void initRfm12bCommon() {
        gpio.spi(0x0000, "Read status register");
        gpio.spi(0x8208, "enable xtal, turn off receiver");
        gpio.spi(0x80D8, "Enable send and receive Registers, 433MHz, 12.5pF");
        gpio.spi(0xA640, "Frequency");
        gpio.spi(0xC6A0, "Bitrate");
        //Empfaengersteuerung
        gpio.spi(0x94C0, "VDI, Fast, 67kHz, 0dBm,-103dBm");
        gpio.spi(0xC2AC, "Data Filter & Clock Recovery");
        gpio.spi(0xCC76, "Taktgenerator");
        //Sendersteuerung
        gpio.spi(0xCA83, "FIFO8, SYNC");
        gpio.spi(0xCED4, "Synch Pattern");
        gpio.spi(0xC487, "Auto Frequency Control");
        gpio.spi(0x9820, "!mp,Frequenzhub=45khz, MAX OUT");
        gpio.spi(0xE000, "Wakeup Timer not used");
        gpio.spi(0xC800, "Low Duty cycle Not use");
        gpio.spi(0xC000, "1.0MHz, 2.2V");

        sleep(30);
        gpio.spi(0x0000, "Read status register");
    }

    private void initRfm12bAsSender() {
        gpio.setnIRQListener(null);

        gpio.spi(0x0000, "Read status register");
        gpio.spi(0x8208, "enable xtal, turn off receiver");
        gpio.spi(0x8008, "Disable FIFO Register");
        gpio.spi(0x80D8, "Enable FIFO Register");
        gpio.spi(0x0000, "Read Status register");

        txmode = TransceiveMode.SENDER;

        System.out.println("Switched Mode to Sender");
    }

    private void initRfm12bAsReceiver() {
        txbuffer.clear();

        gpio.spi(0x0000, "Read status register");
        gpio.spi(0x82D8, "enable receive, !PA");
        gpio.spi(0x0000, "Read Status register");

        txmode = TransceiveMode.RECEIVER;
        gpio.setnIRQListener(this);

        System.out.println("Switched Mode to Receiver");
    }

    private boolean isReadyToSend() {
        return gpio.nIRQisHigh();
    }

    private void sendDataByte(int data, String text) {
        boolean sendRegisterExpectsByte = false;
        while (!sendRegisterExpectsByte) {
            int stat = gpio.spi(0x0000, "Read Status register");
            sendRegisterExpectsByte = (stat & 0b1000000000000000) > 0;
        }
        gpio.spi(0xB800 | data, text);
    }


}
