package com.dnd.modutime.core.feedback.controller.dto;

import com.dnd.modutime.core.feedback.application.command.FeedbackAuthor;
import com.dnd.modutime.core.feedback.application.command.FeedbackCreateCommand;
import com.dnd.modutime.core.feedback.domain.FeedbackCategory;
import com.dnd.modutime.core.feedback.domain.ResponsePair;
import com.dnd.modutime.core.feedback.domain.Responses;
import com.dnd.modutime.core.feedback.domain.Snapshot;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 피드백 제출 요청 본문. 프론트(PostFeedbackRequestBody)가 설계한 계약을 그대로 받는다.
 */
public record FeedbackRequest(

        @NotNull(message = "category는 필수값입니다.")
        FeedbackCategory category,

        @NotBlank(message = "content는 필수값입니다.")
        @Size(max = 1000, message = "content는 최대 1000자까지 가능합니다.")
        String content,

        @Email(message = "replyEmail 형식이 올바르지 않습니다.")
        String replyEmail,

        @NotNull(message = "interview는 필수값입니다.")
        @Valid
        InterviewRequest interview,

        @NotEmpty(message = "responses는 최소 1개 이상이어야 합니다.")
        @Valid
        List<ResponsePairRequest> responses,

        @NotNull(message = "snapshot은 필수값입니다.")
        @Valid
        SnapshotRequest snapshot
) {

    public FeedbackCreateCommand toCommand(FeedbackAuthor author) {
        return new FeedbackCreateCommand(
                category,
                content.trim(),
                emptyToNull(replyEmail),
                interview.agreed(),
                interview.phoneNumber(),
                toResponses(),
                snapshot.toSnapshot(),
                author
        );
    }

    private Responses toResponses() {
        List<ResponsePair> pairs = responses.stream()
                .map(it -> new ResponsePair(it.question(), it.answer()))
                .collect(Collectors.toList());
        return new Responses(pairs);
    }

    private static String emptyToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value;
    }

    public record InterviewRequest(

            @NotNull(message = "interview.agreed는 필수값입니다.")
            Boolean agreed,

            @javax.validation.constraints.Pattern(
                    regexp = "^01[016789]-?\\d{3,4}-?\\d{4}$",
                    message = "interview.phoneNumber 형식이 올바르지 않습니다."
            )
            String phoneNumber
    ) {
    }

    public record ResponsePairRequest(

            @NotBlank(message = "responses[].question은 필수값입니다.")
            String question,

            String answer
    ) {
    }

    public record SnapshotRequest(

            @Valid
            UserRequest user,

            @Valid
            RoomRequest room,

            @NotNull(message = "snapshot.page는 필수값입니다.")
            @Valid
            PageRequest page,

            @NotNull(message = "snapshot.env는 필수값입니다.")
            @Valid
            EnvRequest env
    ) {

        public Snapshot toSnapshot() {
            return new Snapshot(
                    user == null ? null : new Snapshot.User(user.name(), user.email()),
                    room == null ? null : new Snapshot.Room(
                            room.roomId(), room.type(), room.title(),
                            room.participantCount(), room.headCount(), room.deadLine()),
                    new Snapshot.Page(page.routeId(), page.pathname(), page.search()),
                    new Snapshot.Env(
                            env.userAgent(),
                            env.viewport() == null ? null
                                    : new Snapshot.Viewport(env.viewport().width(), env.viewport().height()),
                            env.appVersion())
            );
        }

        public record UserRequest(String name, String email) {
        }

        public record RoomRequest(
                String roomId,
                String type,
                String title,
                Integer participantCount,
                Integer headCount,
                String deadLine
        ) {
        }

        public record PageRequest(String routeId, String pathname, String search) {
        }

        public record EnvRequest(String userAgent, @Valid ViewportRequest viewport, String appVersion) {
        }

        public record ViewportRequest(Integer width, Integer height) {
        }
    }
}
