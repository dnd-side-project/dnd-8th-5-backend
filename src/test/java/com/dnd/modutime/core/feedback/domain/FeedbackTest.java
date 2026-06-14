package com.dnd.modutime.core.feedback.domain;

import com.dnd.modutime.core.feedback.domain.converter.ResponsesJsonConverter;
import com.dnd.modutime.core.feedback.domain.converter.SnapshotJsonConverter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FeedbackTest {

    @DisplayName("필수값이 채워진 피드백을 생성한다")
    @Test
    void 피드백을_생성한다() {
        Feedback feedback = new Feedback(
                FeedbackCategory.BUG,
                "일정 제출 버튼이 안 눌려요.",
                "user@example.com",
                true,
                "010-1234-5678",
                new Responses(List.of(new ResponsePair("무엇이 문제인가요?", "버튼이 안 눌려요"))),
                new Snapshot(new Snapshot.User("김모두", "user@example.com"), null,
                        new Snapshot.Page("/$roomId/schedules", "/abc/schedules", ""),
                        new Snapshot.Env("UA", new Snapshot.Viewport(390, 844), "2.0.0")),
                AuthorType.MEMBER,
                42L,
                "김모두",
                "user@example.com"
        );

        assertThat(feedback.getCategory()).isEqualTo(FeedbackCategory.BUG);
        assertThat(feedback.getAuthorType()).isEqualTo(AuthorType.MEMBER);
        assertThat(feedback.getAuthorUserId()).isEqualTo(42L);
        assertThat(feedback.isInterviewAgreed()).isTrue();
    }

    @DisplayName("content가 비어있으면 생성에 실패한다")
    @Test
    void content가_비면_예외() {
        assertThatThrownBy(() -> new Feedback(
                FeedbackCategory.PRAISE, "  ", null, false, null,
                new Responses(List.of(new ResponsePair("q", "a"))),
                new Snapshot(null, null,
                        new Snapshot.Page("r", "p", ""),
                        new Snapshot.Env("UA", new Snapshot.Viewport(1, 2), "2.0.0")),
                AuthorType.ANONYMOUS, null, null, null
        )).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("responses가 비어있으면 Responses 생성에 실패한다")
    @Test
    void responses가_비면_예외() {
        assertThatThrownBy(() -> new Responses(List.of()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("MEMBER인데 authorUserId/authorEmail이 없으면 생성에 실패한다")
    @Test
    void member_식별자_누락_예외() {
        assertThatThrownBy(() -> feedbackWithAuthor(AuthorType.MEMBER, null, "김카카오", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("ANONYMOUS인데 작성자 식별 정보가 있으면 생성에 실패한다")
    @Test
    void anonymous_식별자_존재_예외() {
        assertThatThrownBy(() -> feedbackWithAuthor(AuthorType.ANONYMOUS, null, "이름있음", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("GUEST인데 authorUserId/email을 가지면 생성에 실패한다")
    @Test
    void guest_회원식별자_보유_예외() {
        assertThatThrownBy(() -> feedbackWithAuthor(AuthorType.GUEST, 1L, "김모두", "g@x.com"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private static Feedback feedbackWithAuthor(AuthorType type, Long userId, String name, String email) {
        return new Feedback(
                FeedbackCategory.PRAISE, "내용", null, false, null,
                new Responses(List.of(new ResponsePair("q", "a"))),
                new Snapshot(null, null,
                        new Snapshot.Page("r", "p", ""),
                        new Snapshot.Env("UA", new Snapshot.Viewport(1, 2), "2.0.0")),
                type, userId, name, email
        );
    }

    @DisplayName("Responses JSON 컨버터는 bare 배열로 직렬화/역직렬화한다")
    @Test
    void responses_컨버터_라운드트립() {
        var converter = new ResponsesJsonConverter();
        var responses = new Responses(List.of(
                new ResponsePair("q1", "a1"),
                new ResponsePair("q2", "a2")
        ));

        String json = converter.convertToDatabaseColumn(responses);
        Responses restored = converter.convertToEntityAttribute(json);

        assertThat(json).startsWith("[").endsWith("]");
        assertThat(restored.values()).containsExactlyElementsOf(responses.values());
    }

    @DisplayName("Snapshot JSON 컨버터는 null 내부 필드를 보존하며 직렬화/역직렬화한다")
    @Test
    void snapshot_컨버터_라운드트립() {
        var converter = new SnapshotJsonConverter();
        var snapshot = new Snapshot(
                null,
                new Snapshot.Room("abc123", "TABLE", "팀 회식", 4, null, null),
                new Snapshot.Page("/$roomId/schedules", "/abc123/schedules", "?share=true"),
                new Snapshot.Env("Mozilla/5.0", new Snapshot.Viewport(390, 844), "2.0.0")
        );

        String json = converter.convertToDatabaseColumn(snapshot);
        Snapshot restored = converter.convertToEntityAttribute(json);

        assertThat(restored).isEqualTo(snapshot);
        assertThat(restored.user()).isNull();
        assertThat(restored.room().headCount()).isNull();
    }
}
