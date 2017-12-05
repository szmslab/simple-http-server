package com.szmslab.simplehttpserver;

import java.io.IOException;
import java.nio.file.Path;

public class HtmlResource extends Resource {

    public HtmlResource(byte[] bytes) {
        super("text/html", bytes);
    }

    public HtmlResource(Path path) throws IOException {
        super("text/html", path);
    }

}
