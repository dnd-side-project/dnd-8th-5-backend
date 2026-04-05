package com.dnd.modutime.core.notification.domain;

import com.dnd.modutime.util.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Map;

@Converter
public class MapToJsonConverter implements AttributeConverter<Map<String, String>, String> {

    private static final TypeReference<Map<String, String>> TYPE_REF = new TypeReference<>() {};

    @Override
    public String convertToDatabaseColumn(Map<String, String> attribute) {
        return JsonUtils.toJsonOrNull(attribute);
    }

    @Override
    public Map<String, String> convertToEntityAttribute(String dbData) {
        return JsonUtils.readValue(dbData, TYPE_REF);
    }
}
