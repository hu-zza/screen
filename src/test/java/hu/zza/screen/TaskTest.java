package hu.zza.screen;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import hu.zza.screen.core.TestManager;

class TaskTest {

    @Nested
    class Screen {
        private final UUID TASK_ID = UUID.fromString("c5ce84c0-4415-4cd8-9c67-0f02b8022ff4");

        @Test
        void getSecondsBetween_testWithScreen() {
            TestManager.validateTask(TASK_ID);
        }
    }

    @ParameterizedTest
    @CsvSource({
            "2020-02-02T20:30:40, 2020-02-02T20:30:50, 9"
    })
    void getSecondsBetween_shouldReturnForValidInput(LocalDateTime from, LocalDateTime to, int expectedSeconds) {
        assertEquals(expectedSeconds, Task.getSecondsBetween(from, to));
    }
}