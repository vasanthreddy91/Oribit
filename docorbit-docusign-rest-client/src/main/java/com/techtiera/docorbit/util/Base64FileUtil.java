package com.techtiera.docorbit.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

public final class Base64FileUtil {

    private Base64FileUtil() {
        // utility class
    }

    /**
     * Converts a file to Base64 string.
     *
     * @param filePath absolute or relative file path
     * @return Base64 encoded file content
     */
    public static String encodeToBase64(String filePath) {
        try {
            byte[] fileBytes = Files.readAllBytes(Path.of(filePath));
            return Base64.getEncoder().encodeToString(fileBytes);
        } catch (IOException ex) {
            throw new IllegalStateException(
                    "Failed to convert file to Base64: " + filePath, ex);
        }
    }

    /**
     * Converts file bytes to Base64 string.
     *
     * @param bytes file content bytes
     * @return Base64 encoded string
     */
    public static String encodeToBase64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }
}
