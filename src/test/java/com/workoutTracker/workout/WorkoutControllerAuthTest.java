package com.workoutTracker.workout;

import com.workoutTracker.auth.AuthController;
import com.workoutTracker.config.SecurityConfig;
import com.workoutTracker.service.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({WorkoutController.class, AuthController.class})
@Import({SecurityConfig.class, TokenService.class})
public class WorkoutControllerAuthTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    WorkoutRepository repository;



    @Test
    void rootReturns401WhenUnauthorized() throws Exception{
        this.mockMvc.perform(get("/"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void workoutsReturnsOKWhenAuthenticated() throws Exception{
        MvcResult tokenResponse = this.mockMvc.perform(post("/token")
                .with(httpBasic("nat", "password")))
                .andExpect(status().isOk())
                .andReturn();
        String token = tokenResponse.getResponse().getContentAsString();

        this.mockMvc.perform(get("/api/workouts")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

    }
}
