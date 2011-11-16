package net.codjo.test.release.test.web;
import net.codjo.util.file.FileUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import junit.framework.Assert;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
/**
 *
 */
public class DummyWebServer {
    private Server jetty;
    private Map<String, String> pages = new HashMap<String, String>();
    private Map<String, String> headers = new HashMap<String, String>();
    private static final int PORT = 8181;


    public void start() throws Exception {
        jetty = startJetty();
        Assert.assertTrue("Server UP", jetty.isStarted());
    }


    public void stop() throws Exception {
        if (jetty != null) {

            headers.clear();
            pages.clear();

            jetty.stop();
            Assert.assertTrue("Server DOWN", jetty.isStopped());
        }
    }


    public void addPage(String pageName, String content) {
        pages.put(pageName, content);
    }


    private Server startJetty() throws Exception {
        Server server = new Server(PORT);
        Context context = new Context(server, "/", Context.SESSIONS);
        context.addServlet(new ServletHolder(new WebTestServlet()), "/*");
        server.start();
        return server;
    }


    public String getUrl(String path) {
        return "http://localhost:" + PORT + "/" + path;
    }


    public Map<String, String> getHeaders() {
        return headers;
    }


    private class WebTestServlet extends HttpServlet {

        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response)
              throws ServletException, IOException {
            String path = request.getRequestURI();

            Enumeration names = request.getHeaderNames();
            while (names.hasMoreElements()) {
                String name = (String)names.nextElement();
                headers.put(name, request.getHeader(name));
            }

            String content = pages.get(path.substring(1));
            if (content == null) {
                content = loadPage(path);
            }
            if (content == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            response.setContentType("text/html");
            response.getWriter().print(content);
            response.setStatus(HttpServletResponse.SC_OK);
        }


        @Override
        protected void doPost(HttpServletRequest request, HttpServletResponse response)
              throws ServletException, IOException {
            StringBuilder sb = new StringBuilder();
            for (Object entry : request.getParameterMap().entrySet()) {
                sb.append(((Map.Entry)entry).getKey());
                sb.append(" = ");
                sb.append(valueToString(((Map.Entry)entry).getValue()));
                sb.append("\n");
            }
            response.setContentType("text/html");
            response.getWriter().print(sb);
            response.setStatus(HttpServletResponse.SC_OK);
        }


        private String valueToString(Object value) {
            if (value instanceof String[]) {
                return Arrays.toString((String[])value);
            }
            else {
                return value.toString();
            }
        }


        private String loadPage(String path) throws IOException {
            InputStream stream = getClass().getResourceAsStream("." + path);
            if (stream == null) {
                return null;
            }
            return FileUtil.loadContent(new InputStreamReader(stream));
        }
    }
}
