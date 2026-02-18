package com.dnd.modutime.core.participant.controller.dto;

import javax.validation.constraints.NotBlank;

public record ParticipantJoinRequest(
        @NotBlank(message = "표시 이름은 필수값 입니다.")
        String name
) {
}
