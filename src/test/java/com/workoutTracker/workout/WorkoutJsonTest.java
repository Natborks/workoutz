package com.workoutTracker.workout;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class WorkoutJsonTest {

    @Autowired
    JacksonTester<Workout> json;
    @Test
    void testWorkoutSerialization() throws IOException {
        Workout workout = new Workout(2L, "squats", 66, BodyPart.LEGS, 8, "sarah1");
        assertThat(json.write(workout)).isStrictlyEqualToJson("expected.json");
    }

    @Test
    void testWorkoutDesrialization() throws IOException {
        String expected = """
                {
                  "ID": 2,
                  "name": "squats",
                  "weight": 66,
                  "bodyPart": "LEGS",
                  "reps": 8,
                  "owner": "sarah1"
                }
                """;
        assertThat(json.parse(expected))
                .isEqualTo(new Workout(2L, "squats", 66, BodyPart.LEGS, 8, "sarah1"));
        assertThat(json.parseObject(expected).ID()).isEqualTo(2L);
        assertThat(json.parseObject(expected).name()).isEqualTo("squats");
        assertThat(json.parseObject(expected).weight()).isEqualTo(66);
        assertThat(json.parseObject(expected).bodyPart()).isEqualTo(BodyPart.LEGS);
        assertThat(json.parseObject(expected).reps()).isEqualTo(8);
    }
}
