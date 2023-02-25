package com.dnd.modutime.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TimeAndCountPerDate {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate availableDate;
    private List<AvailableTimeInfo> availableTimeInfos;
}
