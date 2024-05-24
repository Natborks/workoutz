package com.workoutTracker;

import com.workoutTracker.workout.BodyPart;
import com.workoutTracker.workout.Workout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class WorkoutTrackerApplication {
	private static final Logger log = LoggerFactory.getLogger(WorkoutTrackerApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(WorkoutTrackerApplication.class, args);
	}

	@Bean
	CommandLineRunner runner() {
		return args -> {
			Workout workout = new Workout(2L, "squats", 66, BodyPart.LEGS, 8, "sarah1");
			log.info(workout.toString());
		};
	}

}
