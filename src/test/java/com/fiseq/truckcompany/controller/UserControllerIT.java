package com.fiseq.truckcompany.controller;

import com.fiseq.truckcompany.TruckCompanyApplication;
import com.fiseq.truckcompany.constants.UserRegistrationErrorMessages;
import com.fiseq.truckcompany.dto.UserDto;
import com.fiseq.truckcompany.dto.UserRegistrationData;
import com.fiseq.truckcompany.repository.UserRepository;
import com.fiseq.truckcompany.service.UserService;
import com.fiseq.truckcompany.utilities.UserMapper;
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
public class UserControllerIT {

    @Autowired
    private UserController underTest;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Test
    public void createNewUserWithAllValidField_ThenSuccessfullyCreated() {

        UserDto user = new UserDto();
        user.setPassword("mockPassword");
        user.setUserName("mockUsername");
        user.setEmail("mockuser@gmail.com");
        user.setFirstName("mock");
        user.setLastName("mock");
        user.setRecoveryQuestionId(3);
        user.setRecoveryAnswer("mockAnswer");

        ResponseEntity<UserRegistrationData> response = underTest.registerUser(user);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        UserRegistrationData registrationData = response.getBody();
        Assertions.assertEquals("mockuser@gmail.com", registrationData.getEmail());
        Assertions.assertEquals("mockUsername", registrationData.getUserName());
    }

    @Test
    public void createNewUserWithEmptyUsernameField_ThenReturnFailure() {

        UserDto user = new UserDto();
        user.setPassword("mockPassword");
        user.setUserName("");
        user.setEmail("mockuser@gmail.com");
        user.setFirstName("mock");
        user.setLastName("mock");
        user.setRecoveryQuestionId(3);
        user.setRecoveryAnswer("mockAnswer");

        ResponseEntity<UserRegistrationData> response = underTest.registerUser(user);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        UserRegistrationData registrationData = response.getBody();
        Assertions.assertEquals(UserRegistrationErrorMessages.FIELDS_CANNOT_BE_EMPTY.getUserText(), registrationData.getErrorMessage());
    }

    @Test
    public void createNewUser_withAllValidFields_butAlreadyExistUsername_ThenReturnFailure() {
        UserDto user = new UserDto();
        user.setPassword("mockPassword");
        user.setUserName("mockUsername");
        user.setEmail("mockuser@gmail.com");
        user.setFirstName("mock");
        user.setLastName("mock");
        user.setRecoveryQuestionId(3);
        user.setRecoveryAnswer("mockAnswer");
        userRepository.save(UserMapper.userDtoToUser(user));

        ResponseEntity<UserRegistrationData> response = underTest.registerUser(user);

        Assertions.assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        UserRegistrationData registrationData = response.getBody();
        Assertions.assertEquals(UserRegistrationErrorMessages.USERNAME_ALREADY_EXIST.getUserText(), registrationData.getErrorMessage());
    }
}
