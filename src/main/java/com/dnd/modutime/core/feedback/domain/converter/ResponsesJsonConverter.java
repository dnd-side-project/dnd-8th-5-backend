package com.dnd.modutime.core.feedback.domain.converter;

import com.dnd.modutime.core.feedback.domain.ResponsePair;
import com.dnd.modutime.core.feedback.domain.Responses;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.List;

/**
 * {@link Responses}를 JSON 배열 문자열(TEXT 컬럼)로 변환한다. 저장 형태는 wrapper 없이 bare array다.
 *
 * <p>스키마 변경에도 기존 저장 데이터를 읽을 수 있도록 역직렬화 시 알 수 없는 필드는 무시한다.</p>
 */
@Converter
public class ResponsesJsonConverter implements AttributeConverter<Responses, String> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private static final TypeReference<List<ResponsePair>> TYPE = new TypeReference<>() {
    };

    @Override
    public String convertToDatabaseColumn(Responses attribute) {
        if (attribute == null) {
            return null;
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(attribute.values());
        } catch (Exception e) {
            throw new IllegalStateException("responses 직렬화에 실패했습니다.", e);
        }
    }

    @Override
    public Responses convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return null;
        }
        try {
            return new Responses(OBJECT_MAPPER.readValue(dbData, TYPE));
        } catch (Exception e) {
            throw new IllegalStateException("responses 역직렬화에 실패했습니다.", e);
        }
    }
}
