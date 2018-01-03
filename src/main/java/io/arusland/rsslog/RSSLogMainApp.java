package io.arusland.rsslog;

import io.javalin.Context;
import io.javalin.Javalin;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

public class RSSLogMainApp {
    private final Logger log = LogManager.getLogger(getClass());
    private static final int DEFAULT_PORT = 8080;

    public static void main(String[] args) {
        RSSLogMainApp app1 = new RSSLogMainApp();

        app1.start();
    }

    private void start() {
        try {
            startInternal();
        }catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    private void startInternal() {
        int port = getPort();

        log.info("Starting on port " + port);

        Javalin app = Javalin.start(port);
        app.get("/hackernews1.rss", this::getHackerNews);
    }

    private void getHackerNews(Context context) {
        long lastTime = System.currentTimeMillis();

        try(InputStream stream = getClass().getResource("/hackernews1.rss").openStream()) {
            String xml = IOUtils.toString(stream, "UTF-8");
            context.contentType("application/rss+xml").result(xml);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        HttpServletRequest request = context.request();

        long elapsed = System.currentTimeMillis() - lastTime;

        String remoteIp = request.getRemoteAddr();
        String remoteHost = request.getRemoteHost();

        log.info(String.format("GET /hackernews1.rss in %d ms, from (ip: %s, host: %s)",
                elapsed, remoteIp, remoteHost));

        logHeaders(request);
    }

    private void logHeaders(HttpServletRequest request) {
        Enumeration<String> headers = request.getHeaderNames();

        while (headers.hasMoreElements()) {
            String header = headers.nextElement();

            if (header != null) {
                log.info(String.format("%s: %s", header, request.getHeader(header)));
            }
        }
    }

    private int getPort() {
        String port = System.getProperty("port");

        if (StringUtils.isNotBlank(port)) {

            try {
                return Integer.parseInt(port);
            } catch (Exception ex) {
                log.error("Failed parsing port: " + port, ex);
            }
        }

        return DEFAULT_PORT;
    }
}
