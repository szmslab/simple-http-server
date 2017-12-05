package com.szmslab.simplehttpserver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultRequestHandler implements RequestHandler {

    private static final Path DOCUMENT_ROOT = Paths.get("public");

    private final Map<HttpStatus, HtmlResource> htmlMap = new ConcurrentHashMap<>();

    public DefaultRequestHandler() {
        putHtmlResource(HttpStatus.BAD_REQUEST, DOCUMENT_ROOT.resolve("400.html"));
        putHtmlResource(HttpStatus.FORBIDDEN, DOCUMENT_ROOT.resolve("403.html"));
        putHtmlResource(HttpStatus.NOT_FOUND, DOCUMENT_ROOT.resolve("404.html"));
        putHtmlResource(HttpStatus.INTERNAL_SERVER_ERROR, DOCUMENT_ROOT.resolve("500.html"));
    }

    @Override
    public HttpResponse handleRequest(HttpRequest request) {
        try {
            if (request == null) {
                return new HttpResponse(HttpStatus.BAD_REQUEST, htmlMap.get(HttpStatus.BAD_REQUEST));
            }

            Path path = Paths.get(DOCUMENT_ROOT.toString(), request.getRequestPath()).normalize();

            if (!path.startsWith(DOCUMENT_ROOT)) {
                return new HttpResponse(HttpStatus.FORBIDDEN, htmlMap.get(HttpStatus.FORBIDDEN));
            }

            if (Files.isRegularFile(path)) {
                return new HttpResponse(HttpStatus.OK, new Resource(MimeDetector.getMimeType(path), path));
            }

            Path indexFilePath = path.resolve("index.html");
            if (Files.isDirectory(path) && Files.exists(indexFilePath)) {
                return new HttpResponse(HttpStatus.OK, new HtmlResource(indexFilePath));
            }

            return new HttpResponse(HttpStatus.NOT_FOUND, htmlMap.get(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            e.printStackTrace();
            return new HttpResponse(HttpStatus.INTERNAL_SERVER_ERROR, htmlMap.get(HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    private void putHtmlResource(HttpStatus status, Path path) {
        try {
            htmlMap.put(status, new HtmlResource(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
