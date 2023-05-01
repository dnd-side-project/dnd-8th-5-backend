package com.dnd.modutime.core.adjustresult.util.convertor;

import com.dnd.modutime.core.adjustresult.application.DateTimeInfoDto;
import com.dnd.modutime.core.adjustresult.domain.CandidateDateTime;
import java.util.List;

public interface CandidateDateTimeConvertor {

    List<CandidateDateTime> convert(List<DateTimeInfoDto> dateTimeInfosDto);
}
