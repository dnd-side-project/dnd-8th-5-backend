package com.dnd.modutime.core.notification.controller.dto;

import javax.validation.constraints.NotBlank;

public record DeviceTokenRequest(
        @NotBlank String token
) {
}
