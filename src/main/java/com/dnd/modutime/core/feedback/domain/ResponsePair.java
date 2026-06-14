package com.dnd.modutime.core.feedback.domain;

/**
 * 사람이 읽기 좋은 질문-답변 쌍. 프론트가 라벨(question)을 설계하며 JSON 블롭으로 저장된다.
 */
public record ResponsePair(String question, String answer) {
}
