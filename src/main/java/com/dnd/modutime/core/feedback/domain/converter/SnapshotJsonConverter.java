package com.dnd.modutime.core.feedback.domain.converter;

import com.dnd.modutime.core.feedback.domain.Snapshot;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * {@link Snapshot}을 JSON 문자열(TEXT 컬럼)로 변환한다. 내부 null 필드는 그대로 보존한다.
 *
 * <p>프론트가 스냅샷 구조를 바꾸거나 {@link Snapshot} 필드가 변경/삭제돼도 기존 저장 데이터를 읽을 수 있도록
 * 역직렬화 시 알 수 없는 필드는 무시한다.</p>
 */
@Converter
public class SnapshotJsonConverter implements AttributeConverter<Snapshot, String> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

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
