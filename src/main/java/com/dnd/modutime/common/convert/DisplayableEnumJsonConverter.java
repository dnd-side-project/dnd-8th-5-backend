package com.dnd.modutime.common.convert;

import com.dnd.modutime.common.DisplayableEnum;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import lombok.NoArgsConstructor;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

@JsonComponent
public class DisplayableEnumJsonConverter {
    public static final String FIELD_NAME_CODE = "code";
    public static final String FIELD_NAME_TEXT = "text";

    public static class Serializer extends JsonSerializer<DisplayableEnum> {
        @Override
        public void serialize(DisplayableEnum value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeStartObject();
            gen.writeStringField(FIELD_NAME_CODE, value.getCode());
            gen.writeStringField(FIELD_NAME_TEXT, value.getText());
            gen.writeEndObject();
        }
    }

    @NoArgsConstructor
    public static class Deserializer<T extends Enum<T>> extends JsonDeserializer<T> implements ContextualDeserializer {
        private Class<T> targetClass;

        public Deserializer(Class<T> targetClass) {
            this.targetClass = targetClass;
        }

        @Override
        public Class<?> handledType() {
            return targetClass;
        }

        @Override
        public Deserializer createContextual(DeserializationContext ctxt, BeanProperty property) {
            return new Deserializer(ctxt.getContextualType().getRawClass());
        }

        @Override
        public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            var node = (JsonNode) p.getCodec().readTree(p);
            if (node.get(FIELD_NAME_CODE) != null) {
                return T.valueOf(targetClass, node.get(FIELD_NAME_CODE).asText());
            }
            return T.valueOf(targetClass, node.asText());
        }
    }
}
