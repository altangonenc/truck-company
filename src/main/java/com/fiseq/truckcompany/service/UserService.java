package com.fiseq.truckcompany.service;

import com.fiseq.truckcompany.constants.UserRegistrationErrorMessages;
import com.fiseq.truckcompany.dto.UserRegistrationData;
import com.fiseq.truckcompany.entities.User;
import com.fiseq.truckcompany.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseEntity<UserRegistrationData> registerUser(User user) {
        UserRegistrationData userRegistrationData = new UserRegistrationData();
        if (isUsernameAlreadExists(user.getUserName())) {
            userRegistrationData.setUserName(user.getUserName());
            userRegistrationData.setEmail(user.getEmail());
            userRegistrationData.setErrorMessage(UserRegistrationErrorMessages.USERNAME_ALREADY_EXIST.getUserText());
            return new ResponseEntity<>(userRegistrationData,HttpStatus.BAD_REQUEST);
        }
        if (isEmailAlreadyExists(user.getEmail())) {
            userRegistrationData.setUserName(user.getUserName());
            userRegistrationData.setEmail(user.getEmail());
            userRegistrationData.setErrorMessage(UserRegistrationErrorMessages.EMAIL_ALREADY_EXISTS.getUserText());
            return new ResponseEntity<>(userRegistrationData,HttpStatus.BAD_REQUEST);
        }

        user.setPassword(encodePassword(user.getPassword()));
        userRepository.save(user);

        userRegistrationData.setEmail(user.getEmail());
        userRegistrationData.setUserName(user.getUserName());

        return new ResponseEntity<>(userRegistrationData,HttpStatus.CREATED);
    }

    private boolean isUsernameAlreadExists(String username) {
        return userRepository.existsByUserName(username);
    }

    private boolean isEmailAlreadyExists(String email) {
        return userRepository.existsByEmail(email);
    }

    private String encodePassword (String password) {
        return passwordEncoder.encode(password);
    }
}
