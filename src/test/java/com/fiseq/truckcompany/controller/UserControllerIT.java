package com.fiseq.truckcompany.controller;

import com.fiseq.truckcompany.TruckCompanyApplication;
import com.fiseq.truckcompany.constants.RecoveryQuestion;
import com.fiseq.truckcompany.constants.UserRegistrationErrorMessages;
import com.fiseq.truckcompany.dto.LoginForm;
import com.fiseq.truckcompany.dto.UserDto;
import com.fiseq.truckcompany.dto.UserInformationDto;
import com.fiseq.truckcompany.dto.UserRegistrationData;
import com.fiseq.truckcompany.repository.UserRepository;
import com.fiseq.truckcompany.service.UserService;
import com.fiseq.truckcompany.utilities.UserMapper;
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

import java.util.ArrayList;


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

    @Before
    public void setUp() {
        // delete test database before each test
        userRepository.deleteAll();
    }

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

    @Test
    public void loginUser_withAllValidCredentials_ThenReturnSuccessAndToken() {
        UserDto user = new UserDto();
        user.setPassword("mockPassword");
        user.setUserName("mockUsername");
        user.setEmail("mockuser@gmail.com");
        user.setFirstName("mock");
        user.setLastName("mock");
        user.setRecoveryQuestionId(3);
        user.setRecoveryAnswer("mockAnswer");
        userRepository.save(UserMapper.userDtoToUser(user));

        LoginForm loginForm = new LoginForm();
        loginForm.setPassword("mockPassword");
        loginForm.setUsername("mockUsername");
        ResponseEntity<String> response = underTest.loginUser(loginForm);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.OK,response.getStatusCode());
    }

    @Test
    public void loginUser_withInvalidCredentials_ThenReturnFailure() {
        //save user
        UserDto user = new UserDto();
        user.setPassword("mockPassword");
        user.setUserName("mockUsername");
        user.setEmail("mockuser@gmail.com");
        user.setFirstName("mock");
        user.setLastName("mock");
        user.setRecoveryQuestionId(3);
        user.setRecoveryAnswer("mockAnswer");
        userRepository.save(UserMapper.userDtoToUser(user));

        //test login
        LoginForm loginForm = new LoginForm();
        loginForm.setPassword("mockPasswordWrong");
        loginForm.setUsername("mockUsername");
        ResponseEntity<String> response = underTest.loginUser(loginForm);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST,response.getStatusCode());
    }

    @Test
    public void getProfile_withValidTokenOfUser_thenReturnProfile() {
        //create a new user in database
        UserDto user = new UserDto();
        user.setPassword("mockPassword");
        user.setUserName("mockUsername");
        user.setEmail("mockuser@gmail.com");
        user.setFirstName("mock");
        user.setLastName("mock");
        user.setRecoveryQuestionId(3);
        user.setRecoveryAnswer("mockAnswer");
        userRepository.save(UserMapper.userDtoToUser(user));
        //get token for this user
        LoginForm loginForm = new LoginForm();
        loginForm.setPassword("mockPassword");
        loginForm.setUsername("mockUsername");
        ResponseEntity<String> tokenResponse = underTest.loginUser(loginForm);
        String token = "Bearer " + tokenResponse.getBody();


        //test
        ResponseEntity<UserInformationDto> response = underTest.getUserProfile(token);
        Assertions.assertEquals(user.getEmail(), response.getBody().getEmail());
        Assertions.assertEquals(user.getUserName(), response.getBody().getUserName());
        Assertions.assertEquals(user.getFirstName(), response.getBody().getFirstName());
        Assertions.assertEquals(user.getLastName(), response.getBody().getLastName());
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void getRecoveryQuestion_withValidToken_thenReturnExactRecoveryQuestionOfUser() {
        //create a new user in database
        UserDto user = new UserDto();
        user.setPassword("mockPassword");
        user.setUserName("mockUsername");
        user.setEmail("mockuser@gmail.com");
        user.setFirstName("mock");
        user.setLastName("mock");
        user.setRecoveryQuestionId(3);
        user.setRecoveryAnswer("mockAnswer");
        userRepository.save(UserMapper.userDtoToUser(user));
        //get token for this user
        LoginForm loginForm = new LoginForm();
        loginForm.setPassword("mockPassword");
        loginForm.setUsername("mockUsername");
        ResponseEntity<String> tokenResponse = underTest.loginUser(loginForm);
        String token = "Bearer " + tokenResponse.getBody();

        ResponseEntity<ArrayList<String>> response = underTest.getRecoveryQuestion(token);
        Assertions.assertEquals(RecoveryQuestion.QUESTION3.getQuestion(), response.getBody().get(0));
    }

    @Test
    public void getRecoveryQuestion_withoutToken_thenReturnAllTheQuestions() {
        ResponseEntity<ArrayList<String>> response = underTest.getRecoveryQuestion("");
        Assertions.assertEquals(RecoveryQuestion.QUESTION1.getQuestion(), response.getBody().get(0));
        Assertions.assertEquals(RecoveryQuestion.QUESTION2.getQuestion(), response.getBody().get(1));
        Assertions.assertEquals(RecoveryQuestion.QUESTION3.getQuestion(), response.getBody().get(2));
        Assertions.assertEquals(RecoveryQuestion.QUESTION4.getQuestion(), response.getBody().get(3));
        Assertions.assertEquals(RecoveryQuestion.QUESTION5.getQuestion(), response.getBody().get(4));
        Assertions.assertEquals(RecoveryQuestion.QUESTION6.getQuestion(), response.getBody().get(5));

    }

    @Test
    public void changePassword_withTheCorrectInformationsOfUser_thenReturnSuccess() {
        //create a new user in database
        UserDto user = new UserDto();
        user.setPassword("mockPassword");
        user.setUserName("mockUsername");
        user.setEmail("mockuser@gmail.com");
        user.setFirstName("mock");
        user.setLastName("mock");
        user.setRecoveryQuestionId(3);
        user.setRecoveryAnswer("mockAnswer");
        userRepository.save(UserMapper.userDtoToUser(user));
        //get token for this user
        LoginForm loginForm = new LoginForm();
        loginForm.setPassword("mockPassword");
        loginForm.setUsername("mockUsername");

        ResponseEntity<String> tokenResponse = underTest.loginUser(loginForm);
        Assertions.assertEquals(HttpStatus.OK, tokenResponse.getStatusCode());

        UserDto userInformationsWithNewPassword = new UserDto();
        //set a different password
        userInformationsWithNewPassword.setPassword("newPassword");
        userInformationsWithNewPassword.setUserName("mockUsername");
        userInformationsWithNewPassword.setEmail("mockuser@gmail.com");
        userInformationsWithNewPassword.setFirstName("mock");
        userInformationsWithNewPassword.setLastName("mock");
        userInformationsWithNewPassword.setRecoveryQuestionId(3);
        userInformationsWithNewPassword.setRecoveryAnswer("mockAnswer");
        ResponseEntity<UserInformationDto> response = underTest.changePassword(userInformationsWithNewPassword);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.OK,response.getStatusCode());
    }

    @Test
    public void changePassword_withIncorrectInformationsOfUser_thenReturnFailure() {
        //create a new user in database
        UserDto user = new UserDto();
        user.setPassword("mockPassword");
        user.setUserName("mockUsername");
        user.setEmail("mockuser@gmail.com");
        user.setFirstName("mock");
        user.setLastName("mock");
        user.setRecoveryQuestionId(3);
        user.setRecoveryAnswer("mockAnswer");
        userRepository.save(UserMapper.userDtoToUser(user));

        UserDto userInformationsWithNewPassword = new UserDto();
        //set a different password
        userInformationsWithNewPassword.setPassword("newPassword");
        userInformationsWithNewPassword.setUserName("mockUsername");
        userInformationsWithNewPassword.setEmail("mockuser@gmail.com");
        userInformationsWithNewPassword.setFirstName("mock");
        userInformationsWithNewPassword.setLastName("mock");
        //setting a WRONG Recovery Question Id
        //It should fail because question id's are not matching.
        userInformationsWithNewPassword.setRecoveryQuestionId(4);
        userInformationsWithNewPassword.setRecoveryAnswer("mockAnswer");
        ResponseEntity<UserInformationDto> response = underTest.changePassword(userInformationsWithNewPassword);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST,response.getStatusCode());
    }

}
