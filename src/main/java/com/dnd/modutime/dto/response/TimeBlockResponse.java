package com.dnd.modutime.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TimeBlockResponse {

    private String name;
    private List<AvailableDateTimeResponse> availableDateTimes;
}
