package com.fiseq.truckcompany.controller;

import com.fiseq.truckcompany.TruckCompanyApplication;
import com.fiseq.truckcompany.dto.LoginForm;
import com.fiseq.truckcompany.dto.TruckDto;
import com.fiseq.truckcompany.dto.UserDto;
import com.fiseq.truckcompany.repository.JobRepository;
import com.fiseq.truckcompany.repository.TruckRepository;
import com.fiseq.truckcompany.repository.UserProfileRepository;
import com.fiseq.truckcompany.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TruckCompanyApplication.class)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
public class GameControllerIT {
    @Autowired
    private GameController underTest;

    @Autowired
    private UserController userController;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private TruckRepository truckRepository;

    @Autowired
    private JobRepository jobRepository;

    @Before
    public void setUp() {
        // delete test database before each test
        userRepository.deleteAll();

    }

    public String getToken() {
        UserDto user = new UserDto();
        user.setPassword("mockPassword");
        user.setUserName("mockUsername");
        user.setEmail("mockuser@gmail.com");
        user.setFirstName("mock");
        user.setLastName("mock");
        user.setRecoveryQuestionId(3);
        user.setRecoveryAnswer("mockAnswer");
        userController.registerUser(user);

        LoginForm loginForm = new LoginForm();
        loginForm.setPassword("mockPassword");
        loginForm.setUsername("mockUsername");
        ResponseEntity<String> tokenResponse = userController.loginUser(loginForm);
        String token = "Bearer " + tokenResponse.getBody();
        return token;
    }

    @Test
    public void getAllTruckModelsWithValidToken_thenReturnSuccess() {

        String token = getToken();

        ResponseEntity<TruckDto> response = underTest.getAllTruckModels(token);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

    }

    @Test
    public void getAllTruckModelsWithInvalidToken_ThenReturnFail() {

        String token = "falseToken";

        ResponseEntity<TruckDto> response = underTest.getAllTruckModels(token);

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

    }



}
