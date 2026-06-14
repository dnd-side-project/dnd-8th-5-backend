package com.dnd.modutime.core.feedback.domain.converter;

import com.dnd.modutime.core.feedback.domain.Snapshot;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * {@link Snapshot}을 JSON 문자열(TEXT 컬럼)로 변환한다. 내부 null 필드는 그대로 보존한다.
 */
@Converter
public class SnapshotJsonConverter implements AttributeConverter<Snapshot, String> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Snapshot attribute) {
        if (attribute == null) {
            return null;
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new IllegalStateException("snapshot 직렬화에 실패했습니다.", e);
        }
    }

    @Override
    public Snapshot convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(dbData, Snapshot.class);
        } catch (Exception e) {
            throw new IllegalStateException("snapshot 역직렬화에 실패했습니다.", e);
        }
    }
}
