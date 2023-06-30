package com.fiseq.truckcompany.service;

import com.fiseq.truckcompany.constants.UserRegistrationErrorMessages;
import com.fiseq.truckcompany.dto.UserDto;
import com.fiseq.truckcompany.dto.UserRegistrationData;
import com.fiseq.truckcompany.entities.User;
import com.fiseq.truckcompany.repository.UserRepository;
import com.fiseq.truckcompany.utilities.UserMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @InjectMocks
    private UserMapper userMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testLoadUserByUsername_ExistingUser() {
        // Arrange
        String username = "testUser";
        User user = new User();
        user.setUserName(username);
        user.setPassword("password");
        Mockito.when(userRepository.findByUserName(username)).thenReturn(user);

        // Act
        org.springframework.security.core.userdetails.UserDetails userDetails = userService.loadUserByUsername(username);

        // Assert
        Assertions.assertEquals(username, userDetails.getUsername());
        Assertions.assertEquals(user.getPassword(), userDetails.getPassword());
    }

    @Test
    public void testLoadUserByUsername_NonExistingUser() {
        // Arrange
        String username = "nonExistingUser";
        Mockito.when(userRepository.findByUserName(username)).thenReturn(null);

        // Act & Assert
        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername(username);
        });
    }

    @Test
    public void testRegisterUser_ValidUserDto() {
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setUserName("testUser");
        userDto.setFirstName("John");
        userDto.setLastName("Doe");
        userDto.setEmail("john.doe@example.com");
        userDto.setPassword("password");
        userDto.setRecoveryQuestionId(1);
        userDto.setRecoveryAnswer("answer");

        Mockito.when(userRepository.existsByUserName(userDto.getUserName())).thenReturn(false);
        Mockito.when(userRepository.existsByEmail(userDto.getEmail())).thenReturn(false);
        Mockito.when(passwordEncoder.encode(Mockito.anyString())).thenReturn("encodedPassword");

        // Act
        ResponseEntity<UserRegistrationData> responseEntity = userService.registerUser(userDto);

        // Assert
        Assertions.assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        Assertions.assertNull(responseEntity.getBody().getErrorMessage());
    }

    @Test
    public void testRegisterUser_EmptyUserDto() {
        // Arrange
        UserDto userDto = new UserDto();

        // Act
        ResponseEntity<UserRegistrationData> responseEntity = userService.registerUser(userDto);

        // Assert
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        Assertions.assertEquals(UserRegistrationErrorMessages.FIELDS_CANNOT_BE_EMPTY.getUserText(), responseEntity.getBody().getErrorMessage());
    }

}