package delivery.infrastructure;

import com.google.inject.*;
import org.restlet.*;
import org.restlet.ext.servlet.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;

@Singleton
public class GuiceRestServlet extends ServerServlet {
    @Inject
    private Injector injector;

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //response.setCharacterEncoding("UTF-8");
        super.service(request, response);
    }

    @Override
    protected Application createApplication(Context parentContext) {
        Application application = injector.getInstance(Application.class);
        application.setContext(parentContext.createChildContext());
        return application;
    }

}
