package main;

import com.google.inject.*;
import delivery.infrastructure.*;
import delivery.webapp.*;
import hardware.*;

import javax.servlet.*;
import java.lang.reflect.*;

public class Main extends com.google.inject.servlet.GuiceServletContextListener {

    @Override
    protected Injector getInjector() {
        return Guice.createInjector(new ServletModule(), new HomeserverWebAppModule(), new RaspiHardwareModule());
    }


    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        getInjector().getInstance(GpioHeader.class).shutdown();

        workaroundToEnableTomcatToUnloadGuice();
        super.contextDestroyed(servletContextEvent);
    }

    private void workaroundToEnableTomcatToUnloadGuice() {
        //see: http://stackoverflow.com/questions/8842256/guice-3-0-tomcat-7-0-classloader-memory-leak
        try {
            Class<?> queueHolderClass = Class.forName("com.google.inject.internal.util.$MapMaker$QueueHolder");
            Field queueField = queueHolderClass.getDeclaredField("queue");
            queueField.setAccessible(true);

            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(queueField, queueField.getModifiers() & ~Modifier.FINAL);

            queueField.set(null, null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
