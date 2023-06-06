package com.fiseq.truckcompany.utilities;

import com.fiseq.truckcompany.dto.UserDto;
import com.fiseq.truckcompany.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    private static PasswordEncoder passwordEncoder;

    @Autowired
    public UserMapper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public static synchronized User userDtoToUser(UserDto userDto) {
        return User.builder()
                .email(userDto.getEmail())
                .userName(userDto.getUserName())
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .recoveryQuestionId(userDto.getRecoveryQuestionId())
                .recoveryAnswer(passwordEncoder.encode(userDto.getRecoveryAnswer()))
                .build();
    }
}
