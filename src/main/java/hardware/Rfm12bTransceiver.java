package hardware;

import com.google.inject.*;

import java.util.logging.*;

import static hardware.HardwareUtils.*;

@Singleton
public class Rfm12bTransceiver {
    private static final Logger LOG = Logger.getLogger(Rfm12bTransceiver.class.getCanonicalName());

    @Inject
    private GpioHeader gpio;
    private TransceiveMode txmode;

    private enum TransceiveMode {
        SENDER,
        RECEIVER
    }

    public void sendpackage(int data) {
        initRfm12bAsSender();
        sendDataToAddress(0x23, data);
    }

    private void initRfm12bAsSender() {
        if (TransceiveMode.SENDER.equals(txmode)) {
            return;
        }
        gpio.spi(0x0000, "Read status register");
        gpio.spi(0x80D8, "Enable send and receive Registers, 433MHz, 12.5pF");
        gpio.spi(0x8208, "enable xtal");
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

        sleep(50);

        gpio.spi(0x0000, "Read Status register");

        txmode = TransceiveMode.SENDER;
    }

    private void sendDataToAddress(int address, int data) {
        while (!isReadyToSend()) {
            sleep(50);
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

        sendDataByte(address, "Adresse als Nutzdaten-Byte senden");
        sendDataByte(address, "Adresse als Nutzdaten-Byte senden");
        sendDataByte(address, "Adresse als Nutzdaten-Byte senden");
        sendDataByte(data, "Nutzdaten Byte senden");
        sendDataByte(data, "Nutzdaten Byte senden");
        sendDataByte(data, "Nutzdaten Byte senden");

        sendDataByte(0xAA, "PREAMBLE senden");
        sendDataByte(0xAA, "PREAMBLE senden");

        gpio.spi(0x8208, "turn off transmitter");
        gpio.spi(0x0000, "Read Status register");
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
