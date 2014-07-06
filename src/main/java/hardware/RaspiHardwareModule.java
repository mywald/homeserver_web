package hardware;

import com.google.inject.*;

public class RaspiHardwareModule extends AbstractModule {

    @Override
    protected void configure() {
        try {
            bind(GpioHeader.class).toConstructor(GpioHeader.class.getConstructor());
        } catch (NoSuchMethodException e) {
            addError(e);
        }
    }
}