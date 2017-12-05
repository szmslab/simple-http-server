package com.szmslab.simplehttpserver;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpResponse {

    private static final String CRLF = "\r\n";

    private final HttpStatus status;

    private final String contentType;

    private final byte[] body;

    public HttpResponse(HttpStatus status, Resource resource) {
        this.status = status;
        this.contentType = resource.getContentType();
        this.body = resource.getBytes();
    }

    public void writeTo(OutputStream out) throws IOException {
        out.write(createHeader().getBytes(StandardCharsets.UTF_8));
        out.write(body);
        out.flush();
    }

    private String createHeader() {
        String statusLine = "HTTP/1.1 " + status;

        Map<String, String> headerMap = new LinkedHashMap<>();
        headerMap.put("Content-Type", contentType);
        headerMap.put("Content-Length", String.valueOf(body.length));
        headerMap.put("Server", "Simple HTTP Server");
        headerMap.put("Date", DateTimeFormatter.RFC_1123_DATE_TIME.format(OffsetDateTime.now(ZoneOffset.UTC)));
        headerMap.put("Connection", "Close");

        String header = headerMap.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining(CRLF));

        return statusLine + CRLF + header + CRLF + CRLF;
    }

}
