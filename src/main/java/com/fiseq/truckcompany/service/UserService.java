package com.fiseq.truckcompany.service;

import com.fiseq.truckcompany.constants.UserRegistrationErrorMessages;
import com.fiseq.truckcompany.dto.UserInformationDto;
import com.fiseq.truckcompany.dto.UserRegistrationData;
import com.fiseq.truckcompany.entities.User;
import com.fiseq.truckcompany.repository.UserRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(username);
        if (user == null) {
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPassword(), new ArrayList<>());
    }

    public ResponseEntity<UserRegistrationData> registerUser(User user) {
        UserRegistrationData userRegistrationData = new UserRegistrationData();
        if (checkPropertiesOfUserNotNullOrEmpty(user)) {
            userRegistrationData.setErrorMessage(UserRegistrationErrorMessages.FIELDS_CANNOT_BE_EMPTY.getUserText());
            return new ResponseEntity<>(userRegistrationData, HttpStatus.BAD_REQUEST);
        }
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

    private boolean checkPropertiesOfUserNotNullOrEmpty (User user) {
        if (StringUtils.isEmpty(user.getUserName())) {
            return true;
        }
        if (StringUtils.isEmpty(user.getLastName())) {
            return true;
        }
        if (StringUtils.isEmpty(user.getEmail())) {
            return true;
        }
        if (StringUtils.isEmpty(user.getFirstName())) {
            return true;
        }
        if (StringUtils.isEmpty(user.getPassword())) {
            return true;
        }
        return false;
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

    public UserInformationDto getUserByUsername (String username) {
        User user = userRepository.findByUserName(username);
        if (user != null) {
            UserInformationDto userInformationDto = new UserInformationDto();
            userInformationDto.setFirstName(user.getFirstName());
            userInformationDto.setLastName(user.getLastName());
            userInformationDto.setEmail(user.getEmail());
            userInformationDto.setUserName(user.getUserName());
            return userInformationDto;
        }
        return null;
    }
}
