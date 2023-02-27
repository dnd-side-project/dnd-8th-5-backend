package com.dnd.modutime.dto.request;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TimeReplaceRequest {

    private String name;
    private List<AvailableDateTimeRequest> availableDateTimes;
}
