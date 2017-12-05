package com.szmslab.simplehttpserver;

public interface RequestHandler {

    HttpResponse handleRequest(HttpRequest request);

}
