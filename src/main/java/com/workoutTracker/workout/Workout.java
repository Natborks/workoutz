package com.workoutTracker.workout;

import org.springframework.data.annotation.Id;

public record Workout(@Id Long ID, String name, float weight, BodyPart bodyPart, int reps, String owner) {
}
