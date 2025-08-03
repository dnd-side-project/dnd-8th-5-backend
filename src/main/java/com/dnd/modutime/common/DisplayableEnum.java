package com.dnd.modutime.common;

/**
 * 화면에 노출되는 Enum 을 일정한 형식으로 처리하기 위한 용도
 * <code>
 * {
 * "code": "CODE",
 * "text": "코드문구"
 * }
 * </code>
 */
public interface DisplayableEnum {
    String getCode();

    String getText();
}
