package hardware;

import com.google.inject.*;
import hardware.spi.*;

public class RaspiHardwareModule extends AbstractModule {

    @Override
    protected void configure() {
        try {
            bind(SpiInterface.class).toConstructor(SpiInterface.class.getConstructor());
        } catch (NoSuchMethodException e) {
            addError(e);
        }
    }
}