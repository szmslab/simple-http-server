package com.szmslab.simplehttpserver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Resource {

    private final String contentType;

    private final byte[] bytes;

    public Resource(String contentType, byte[] bytes) {
        this.contentType = contentType;
        this.bytes = bytes;
    }

    public Resource(String contentType, Path path) throws IOException {
        this.contentType = contentType;
        this.bytes = Files.readAllBytes(path);
    }

    public String getContentType() {
        return contentType;
    }

    public byte[] getBytes() {
        return bytes;
    }

}
