package com.dnd.modutime.core.auth.application;

import com.dnd.modutime.core.auth.application.response.GuestLoginResponse;
import com.dnd.modutime.core.participant.application.ParticipantFacade;
import com.dnd.modutime.core.participant.application.command.ParticipantCreateCommand;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class GuestAuthFacade {

    private final ParticipantFacade participantFacade;
    private final GuestTokenProvider guestTokenProvider;

    public GuestAuthFacade(ParticipantFacade participantFacade,
                           GuestTokenProvider guestTokenProvider) {
        this.participantFacade = participantFacade;
        this.guestTokenProvider = guestTokenProvider;
    }

    public GuestLoginResponse login(ParticipantCreateCommand command) {
        participantFacade.login(command);
        var accessToken = guestTokenProvider.createAccessToken(command.getRoomUuid(), command.getName());
        var expireTime = guestTokenProvider.createAccessTokenExpireTime();
        var accessTokenExpireTime = expireTime.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        return new GuestLoginResponse(accessToken, accessTokenExpireTime);
    }
}
