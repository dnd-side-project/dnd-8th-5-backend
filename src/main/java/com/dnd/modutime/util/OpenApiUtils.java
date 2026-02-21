package com.dnd.modutime.util;

import org.springframework.core.io.ClassPathResource;
import org.springframework.lang.Nullable;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class OpenApiUtils {

    private OpenApiUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static String getDocsFromPaths(String[] paths) {
        String docs = "";

        if (paths != null) {
            for (String path : paths) {
                docs += getDocsFromClassPath(path);
            }
        }

        return docs;
    }

    @Nullable
    public static String getDocsFromClassPath(String path) {
        String docs = "\n\n";

        ClassPathResource resource = new ClassPathResource(path);
        if (resource.exists()) {
            try {
                docs += FileCopyUtils.copyToString(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return docs;
    }
}
