package com.dnd.modutime.core.adjustresult.util.executor;

import com.dnd.modutime.core.participant.application.ParticipantQueryService;
import com.dnd.modutime.core.participant.domain.Participant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static com.dnd.modutime.fixture.RoomRequestFixture.ROOM_UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@SpringBootTest
public class AdjustmentResultExecutorFactoryTest {

    @Autowired
    private AdjustmentResultExecutorFactory adjustmentResultExecutorFactory;

    @MockBean
    private ParticipantQueryService participantQueryService;

    @BeforeEach
    void setUp() {
        given(participantQueryService.getByRoomUuid(ROOM_UUID)).willReturn(
                List.of(new Participant(ROOM_UUID, "김동호", "1234"),
                        new Participant(ROOM_UUID, "이수진", "1234"))
        );
    }

    @Test
    void 참여자이름들이_빈_리스트로_넘어오면_AdjustmentResultExecutor를_반환한다() {
        AdjustmentResultResponseGenerator instance = adjustmentResultExecutorFactory.getInstance(ROOM_UUID, List.of());
        assertThat(instance).isInstanceOf(AdjustmentResponseGenerator.class);
    }

    @Test
    void 참여자이름들이_전체참여자로_넘어오면_AdjustmentResultExecutor를_반환한다() {
        AdjustmentResultResponseGenerator instance = adjustmentResultExecutorFactory.getInstance(ROOM_UUID, List.of("김동호", "이수진"));
        assertThat(instance).isInstanceOf(AdjustmentResponseGenerator.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"김동호", "이수진"})
    void 참여자이름들이_일부만_넘어오면_TimeTableResultExecutor를_반환한다(String name) {
        AdjustmentResultResponseGenerator instance = adjustmentResultExecutorFactory.getInstance(ROOM_UUID, List.of(name));
        assertThat(instance).isInstanceOf(TimeTableResponseGenerator.class);
    }

    @Test
    void 참여자이름에_null이_들어오면_예외를_반환한다() {
        assertThatThrownBy(() -> adjustmentResultExecutorFactory.getInstance(ROOM_UUID, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 방에_존재하지않는_참여자이름이_들어오면_예외를_반환한다() {
        assertThatThrownBy(() -> adjustmentResultExecutorFactory.getInstance(ROOM_UUID, List.of("이채민")))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
