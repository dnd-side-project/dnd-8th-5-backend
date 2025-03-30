package com.dnd.modutime.core.adjustresult.util.executor;

import com.dnd.modutime.annotations.MockTest;
import com.dnd.modutime.core.participant.domain.Participant;
import com.dnd.modutime.core.participant.repository.ParticipantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;

import java.util.List;
import java.util.Map;

import static com.dnd.modutime.fixture.RoomRequestFixture.ROOM_UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@MockTest
public class AdjustmentResultExecutorFactoryTest {

    private AdjustmentResultExecutorFactory adjustmentResultExecutorFactory;

    @Mock
    private ParticipantRepository participantRepository;

    @BeforeEach
    void setUp() {
        given(participantRepository.findByRoomUuid(ROOM_UUID)).willReturn(
                List.of(new Participant(ROOM_UUID, "김동호", "1234"),
                        new Participant(ROOM_UUID, "이수진", "1234"))
        );

        var adjustmentResponseGenerator = new AdjustmentResponseGenerator(null, null, null);
        var timeTableResponseGenerator = new TimeTableResponseGenerator(null, null, null, null);
        adjustmentResultExecutorFactory = new AdjustmentResultExecutorFactory(
                Map.of("adjustmentResponseGenerator", adjustmentResponseGenerator,
                        "timeTableResponseGenerator", timeTableResponseGenerator),
                participantRepository
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
