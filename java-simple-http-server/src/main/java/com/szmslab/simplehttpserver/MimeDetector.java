package com.szmslab.simplehttpserver;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Properties;

public class MimeDetector {

    private static final String RESOURCE_PATH = "mime.properties";

    private static final String DEFAULT_MIME_TYPE = "application/octet-stream";

    private static final Properties props = new Properties();

    private MimeDetector() {
    }

    static {
        try (InputStreamReader reader = new InputStreamReader(
                MimeDetector.class.getClassLoader().getResourceAsStream(RESOURCE_PATH), StandardCharsets.UTF_8)) {
            props.load(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getMimeType(Path path) {
        String fileName = path.getFileName().toString();
        return props.getProperty(getExtension(fileName), DEFAULT_MIME_TYPE);
    }

    private static String getExtension(String fileName) {
        int idx = fileName.lastIndexOf(".");
        return idx >= 0 ? fileName.substring(idx + 1) : "";
    }

}
