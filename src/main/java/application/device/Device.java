package application.device;

public class Device {
    private int address;

    public Device(int address) {
        this.address = address;
    }

    public Integer asInt() {
        return address;
    }
}
