package com.dnd.modutime.documentation;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

import static com.dnd.modutime.documentation.MockMvcFactory.MOCK_MVC_MAPPER;

/**
 * 테스트환경에서 사용하는 {@link com.dnd.modutime.util.JsonUtils}
 */
public class TestJsonUtils {

    public static <T> T readValue(String json, Class<T> clazz) {
        if (json == null || json.isBlank()) {
            return null;
        }

        try {
            return MOCK_MVC_MAPPER.readValue(json, clazz);
        } catch (IOException e) {
            throw new JsonDecodeException(e);
        }
    }

    public static <T> T readValue(String json, TypeReference<T> typeReference) {
        if (json == null || json.isBlank()) {
            return null;
        }

        try {
            return MOCK_MVC_MAPPER.readValue(json, typeReference);
        } catch (IOException e) {
            throw new JsonDecodeException(e);
        }
    }

    public static <T> T fromJsonFile(String resourceClasspath, Class<T> clazz) {
        if (resourceClasspath == null) {
            return null;
        }

        var classpathResource = new ClassPathResource(resourceClasspath);
        try {
            return MOCK_MVC_MAPPER.readValue(classpathResource.getInputStream(), clazz);
        } catch (IOException e) {
            throw new JsonDecodeException(e);
        }
    }

    public static <T> T fromJsonFile(String resourceClasspath, TypeReference<T> typeReference) {
        if (resourceClasspath == null) {
            return null;
        }

        var classpathResource = new ClassPathResource(resourceClasspath);
        try {
            return MOCK_MVC_MAPPER.readValue(classpathResource.getInputStream(), typeReference);
        } catch (IOException e) {
            throw new JsonDecodeException(e);
        }
    }

    public static String toPrettyJson(final Object object) {
        try {
            return MOCK_MVC_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (IOException e) {
            throw new JsonEncodeException(e);
        }
    }

    public static String writeAsStringFromJsonFile(String resourceClasspath) {
        if (resourceClasspath == null) {
            return null;
        }

        var classpathResource = new ClassPathResource(resourceClasspath);
        try {
            return toPrettyJson(MOCK_MVC_MAPPER.readTree(classpathResource.getFile()));
        } catch (IOException e) {
            throw new JsonDecodeException(e);
        }
    }

    public static class JsonEncodeException extends RuntimeException {
        private static final long serialVersionUID = 4975703115049362769L;

        public JsonEncodeException(Throwable cause) {
            super(cause);
        }
    }

    public static class JsonDecodeException extends RuntimeException {
        private static final long serialVersionUID = -2651564042039413190L;

        public JsonDecodeException(Throwable cause) {
            super(cause);
        }
    }
}
