package com.workoutTracker;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.workoutTracker.workout.BodyPart;
import com.workoutTracker.workout.Workout;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.security.test.context.support.WithMockUser;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@WithMockUser
class WorkoutTrackerApplicationTests {
	public static final String API_WORKOUTS = "/api/workouts";
	@Autowired
	TestRestTemplate testRestTemplate;

	@Test
	void contextLoads() {
	}


	@Test
	void shouldReturnAWorkout() {
		ResponseEntity<Workout> readResponse = testRestTemplate.getForEntity("/api/workouts/2", Workout.class);
		assertThat(readResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		Workout expected = new Workout(2L, "squats", 66, BodyPart.LEGS, 8, "sarah1");

//		makeAssertionsForWorkoutFields(expected, readResponse);
	}

	@Test
	void shouldNotReturnAWorkoutForInvalidID() {
		ResponseEntity<String> response = testRestTemplate
				.getForEntity("/api/workouts/9999", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(response.getBody()).isBlank();
	}

	@Test
	@DirtiesContext
	void shouldCreateANewWorkout() {
		Workout workout = new Workout(null, "squats", 66, BodyPart.LEGS, 8, "sarah1");
		ResponseEntity<Void> createResponse = testRestTemplate
				.postForEntity(API_WORKOUTS, workout, Void.class);

		assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		URI locationOfNewWorkout = createResponse.getHeaders().getLocation();
		ResponseEntity<String> newWorkoutResponse = testRestTemplate
				.getForEntity(locationOfNewWorkout, String.class);
		System.out.println(locationOfNewWorkout.toString());
		assertThat(newWorkoutResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		makeAssertionsForWorkoutFields(workout, newWorkoutResponse);
	}

	@Test
	public void shouldReturnAListOfWorkouts() {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		ResponseEntity<Workout> workouts = testRestTemplate
				.exchange(API_WORKOUTS, HttpMethod.GET, new HttpEntity<>(httpHeaders), Workout.class);

		assertThat(workouts.getStatusCode()).isEqualTo(HttpStatus.OK);
		DocumentContext documentContext = JsonPath.parse(workouts.getBody());
		System.out.println(workouts.getBody());
		int workoutCount = documentContext.read("$.length()");
		JSONArray ids = documentContext.read("$..ID");
		JSONArray weights = documentContext.read("$..weight");
		JSONArray names = documentContext.read("$..name");
		JSONArray bodyParts = documentContext.read("$..bodyPart");
		JSONArray reps = documentContext.read("$..reps");

		assertThat(workoutCount).isEqualTo(3);
		assertThat(ids).containsExactlyInAnyOrder(2, 3, 5);
		assertThat(weights).containsExactlyInAnyOrder(66.00, 66.00, 100.00);
		assertThat(names).containsExactlyInAnyOrder("squats", "Bench Press", "Shoulder Press");
		assertThat(bodyParts).containsExactlyInAnyOrder(BodyPart.LEGS.toString(), BodyPart.CHEST.toString(), BodyPart.LEGS.toString());
		assertThat(reps).containsExactlyInAnyOrder(8, 8, 8);
	}

	@Test
	@DirtiesContext
	public void shouldUpdateAnExistingResource() {
		Workout workout = new Workout(null, "squats", 66, BodyPart.LEGS, 10, "sarah1");
		HttpEntity<Workout> request = new HttpEntity<>(workout);

		ResponseEntity<Void> response = testRestTemplate
				.exchange(API_WORKOUTS + "/2", HttpMethod.PUT, request, Void.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		ResponseEntity<String> getResponse = testRestTemplate
				.getForEntity(API_WORKOUTS + "/2", String.class);

		makeAssertionsForWorkoutFields(workout, getResponse);

	}

	@Test
	void shouldNotUpdatedACardThatDoesNotExist() {
		Workout workout = new Workout(null, "squats", 66, BodyPart.LEGS, 10, "sarah1");
		HttpEntity<Workout> request = new HttpEntity<>(workout);

		ResponseEntity<Void> response = testRestTemplate
				.exchange(API_WORKOUTS + "/2444", HttpMethod.PUT, request, Void.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	@DirtiesContext
	public void shouldDeleteWorkoutWithGivenId() {
		ResponseEntity<Void> response = testRestTemplate
				.exchange(API_WORKOUTS + "/2", HttpMethod.DELETE, null, Void.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		ResponseEntity getResponse = testRestTemplate
				.getForEntity(API_WORKOUTS + "/2", String.class);

		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	private static void makeAssertionsForWorkoutFields(Workout workout, ResponseEntity<String> getResponse) {
		DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
		Number id = documentContext.read("$.ID");
		String name = documentContext.read("$.name");
		double weight = documentContext.read("$.weight");
		int reps = documentContext.read("$.reps");
		String bodyPart = documentContext.read("$.bodyPart");

		assertThat(id).isNotNull();
		assertThat(reps).isEqualTo(workout.reps());
		assertThat(name).isEqualTo(workout.name());
		assertThat(weight).isEqualTo(workout.weight());
		assertThat(bodyPart).isEqualTo(workout.bodyPart().toString());
	}



}
