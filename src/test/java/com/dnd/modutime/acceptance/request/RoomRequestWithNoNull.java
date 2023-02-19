package com.dnd.modutime.acceptance.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.util.List;

public class RoomRequestWithNoNull {
    private String title;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private List<LocalDate> dates;

    public RoomRequestWithNoNull() {
    }

    public RoomRequestWithNoNull(String title, List<LocalDate> dates) {
        this.title = title;
        this.dates = dates;
    }

    public String getTitle() {
        return title;
    }

    public List<LocalDate> getDates() {
        return dates;
    }
}
