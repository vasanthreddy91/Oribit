package com.techtiera.docorbit.util;

public final class FileExtensionUtil {

    private FileExtensionUtil() {
        // utility class
    }

    /**
     * Returns file extension without dot.
     * Example: "contract.final.PDF" -> "pdf"
     */
    public static String fileExtension(String fileName) {

        if (fileName == null || fileName.isBlank()) {
            return null;
        }

        int lastDot = fileName.lastIndexOf('.');

        // no dot or dot at end
        if (lastDot < 0 || lastDot == fileName.length() - 1) {
            return null;
        }

        return fileName.substring(lastDot + 1).toLowerCase();
    }
}
