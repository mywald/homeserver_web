package delivery.infrastructure;

public class ServletModule extends com.google.inject.servlet.ServletModule {

    @Override
    protected void configureServlets() {
        serve("/app/*").with(GuiceRestServlet.class);
    }
}
