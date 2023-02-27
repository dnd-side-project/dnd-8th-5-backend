package com.dnd.modutime.dto.response;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TimeBlockResponse {

    private String name;
    private List<AvailableDateTimeResponse> availableDateTimes;
}
