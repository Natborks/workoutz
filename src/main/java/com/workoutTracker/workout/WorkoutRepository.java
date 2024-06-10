package com.workoutTracker.workout;

import org.springframework.context.annotation.Bean;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;



@Repository
interface WorkoutRepository extends CrudRepository<Workout, Long> {

}
