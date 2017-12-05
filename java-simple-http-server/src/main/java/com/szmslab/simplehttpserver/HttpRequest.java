package com.szmslab.simplehttpserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpRequest {

    private static final Pattern REQUEST_LINE_PATTERN = Pattern.compile("(?<method>.*) (?<requestUri>.*) (?<httpVersion>.*)");

    private final String rawRequest;

    private final String method;

    private final String requestUri;

    private final String httpVersion;

    public HttpRequest(String rawRequest, String method, String requestUri, String httpVersion) {
        this.rawRequest = rawRequest;
        this.method = method;
        this.requestUri = requestUri;
        this.httpVersion = httpVersion;
    }

    public static HttpRequest parse(InputStream inputStream) throws IOException {
        int intervalMilliseconds = 10;
        int retryMaxCount = 100;
        int retryCount = 0;
        String rawRequest = null;
        while (true) {
            if (retryCount > retryMaxCount) break;

            int available = inputStream.available();
            if (available > 0) {
                byte[] bytes = new byte[available];
                inputStream.read(bytes);
                rawRequest = new String(bytes, StandardCharsets.UTF_8);
                break;
            }

            try {
                TimeUnit.MILLISECONDS.sleep(intervalMilliseconds);
            } catch (InterruptedException ignored) {
            }

            retryCount++;
        }

        if (rawRequest == null) return null;

        try (BufferedReader reader = new BufferedReader(new StringReader(rawRequest))) {
            String requestLine = reader.readLine();
            Matcher matcher = REQUEST_LINE_PATTERN.matcher(requestLine);
            if (matcher.matches()) {
                return new HttpRequest(
                        rawRequest,
                        matcher.group("method"),
                        matcher.group("requestUri"),
                        matcher.group("httpVersion"));
            }
        }

        return null;
    }

    public String getRawRequest() {
        return rawRequest;
    }

    public String getMethod() {
        return method;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public String getRequestPath() {
        int idx = requestUri.indexOf("?");
        return idx >= 0 ? requestUri.substring(0, idx) : requestUri;
    }

    @Override
    public String toString() {
        return rawRequest;
    }

}
