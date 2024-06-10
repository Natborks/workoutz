package com.workoutTracker.workout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/api/workouts")
public class WorkoutController {

    private WorkoutRepository workoutRepository;

    public WorkoutController(WorkoutRepository workoutRepository) {
        this.workoutRepository = workoutRepository;
    }

    @GetMapping
    public ResponseEntity<Iterable<Workout>> getWorkouts() {
        System.out.println(workoutRepository.findAll());
        return ResponseEntity.ok(workoutRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Workout> getWorkoutById(@PathVariable int id) {
        Optional<Workout> workout = workoutRepository.findById((long) id);

        if (workout.isPresent()) {
            return ResponseEntity.ok(workout.get());
        }else return ResponseEntity.notFound().build();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ResponseEntity<Void> createWorkout(@RequestBody Workout workout, UriComponentsBuilder ucb) {
        Workout newWorkout =  workoutRepository.save(workout);
        URI locationOfNewCashCard = ucb.path("/api/workouts/{id}")
                        .buildAndExpand(newWorkout.ID())
                .toUri();

        return ResponseEntity.created(locationOfNewCashCard).build();
    }

    @PutMapping("/{requestedId}")
    public ResponseEntity<Void> updateExistingWorkout(@RequestBody Workout workout,
                                                      @PathVariable Long requestedId)
            throws IllegalArgumentException{
        Optional<Workout> workoutToUpdate = workoutRepository.findById(requestedId);
        if(workoutToUpdate.isPresent()) {
            Workout w = workoutToUpdate.get();
            Workout updatedWorkout = new Workout(w.ID(),
                    workout.name(),
                    workout.weight(),
                    workout.bodyPart(),
                    workout.reps(),
                    workout.owner()
            );
            workoutRepository.save(updatedWorkout);
            return ResponseEntity.noContent().build();
        } else return ResponseEntity.notFound().build();

    }

    @DeleteMapping("/{requestedId}")
    public ResponseEntity<Void> deleteWorkout(@PathVariable Long requestedId) {
        workoutRepository.deleteById(requestedId);
        return ResponseEntity.noContent().build();
    }
}
