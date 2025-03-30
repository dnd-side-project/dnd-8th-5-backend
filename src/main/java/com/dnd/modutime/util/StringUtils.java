package com.dnd.modutime.util;

import com.dnd.modutime.core.common.Constants;
import lombok.extern.slf4j.Slf4j;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
public class StringUtils {
    public static final String REPLACEMENT_IP_MASKING = "***";

    private StringUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static String substringWithCodePoint(String originString, int length) {
        if (isEmpty(originString) || length < 1) {
            return originString;
        }

        int codePointCount = originString.codePointCount(0, originString.length());
        if (codePointCount <= length) {
            return originString;
        }

        return originString.substring(0, originString.offsetByCodePoints(0, length));
    }

    public static String removeWhitespaces(String originString) {
        return isEmpty(originString)
                ? originString
                : originString.replaceAll(" ", "").trim(); // NOSONAR
    }

    public static String escapeCsvItem(String item) {
        String escaped = item.replaceAll("\\R", " ");
        if (item.contains(",") || item.contains("\"") || item.contains("'")) {
            item = item.replace("\"", "\"\"");
            escaped = "\"" + item + "\"";
        }
        return escaped;
    }

    public static String safeUrlEncoding(String value) {
        StringBuilder sb = new StringBuilder(value.length());

        value.codePoints()
                .forEach(codePoint -> {
                    if (codePoint < 128) {
                        sb.appendCodePoint(codePoint);
                    } else {
                        sb.append(
                                URLEncoder.encode(
                                        new String(Character.toChars(codePoint)),
                                        StandardCharsets.UTF_8));
                    }
                });

        return sb.toString();
    }

    /**
     * 정규식 확인
     *
     * @param source 원천
     * @param regExp 정규식
     * @return true: 정규식 만족/false: 정규식 불만족
     */
    public static boolean validRegExp(String source, String regExp) {
        return isNotEmpty(source) && source.matches(regExp);
    }

    /**
     * 정기식 검증 or 기본값 반환
     *
     * @param source        원천
     * @param regExp        정규식표현
     * @param defaultReturn 기본 반환값
     * @return true: 검증 통과/false: 검증 실패
     */
    public static boolean validRegExpOrDefault(String source, String regExp, boolean defaultReturn) {
        if (isEmpty(source)) {
            return defaultReturn;
        }

        return validRegExp(source, regExp);
    }

    /**
     * 문자열 길이검증
     *
     * @param source        원천
     * @param maxLength     최대 길이
     * @param defaultReturn 기본 반환값
     * @return true: 검증 통과/false: 검증 실패
     */
    public static boolean validLengthOrDefault(String source, int maxLength, boolean defaultReturn) {
        if (isEmpty(source)) {
            return defaultReturn;
        }

        return source.length() <= maxLength;
    }

    /**
     * ip를 마스킹 처리한다.
     *
     * @param plainText 마스킹 처리 대상 ip
     * @return 마스킹 처리된 ip
     */
    public static String maskIp(String plainText) {
        return plainText.replaceFirst(Constants.REG_EXP_IP_ADDRESS, REPLACEMENT_IP_MASKING);
    }

    /**
     * 문자열이 64 비트 정수로 표현 가능한지 체크하는 용도.
     *
     * @param input
     * @return
     */
    public static boolean isLongConvertible(String input) {
        try {
            Long.parseLong(input);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

