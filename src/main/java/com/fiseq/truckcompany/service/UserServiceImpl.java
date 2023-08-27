package com.fiseq.truckcompany.service;

import com.fiseq.truckcompany.constants.GameConstants;
import com.fiseq.truckcompany.constants.RecoveryQuestion;
import com.fiseq.truckcompany.constants.SecurityConstants;
import com.fiseq.truckcompany.constants.UserRegistrationErrorMessages;
import com.fiseq.truckcompany.dto.*;
import com.fiseq.truckcompany.entities.User;
import com.fiseq.truckcompany.entities.UserProfile;
import com.fiseq.truckcompany.exception.ChangePasswordException;
import com.fiseq.truckcompany.exception.InvalidAuthException;
import com.fiseq.truckcompany.repository.UserProfileRepository;
import com.fiseq.truckcompany.repository.UserRepository;
import com.fiseq.truckcompany.utilities.UserMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserProfileRepository userProfileRepository, PasswordEncoder passwordEncoder, @Lazy AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(username);
        if (user == null) {
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPassword(), new ArrayList<>());
    }

    public ResponseEntity<UserRegistrationData> registerUser(UserDto userDto) {
        UserRegistrationData userRegistrationData = new UserRegistrationData();
        if (checkPropertiesOfUserNullOrEmpty(userDto)) {
            userRegistrationData.setErrorMessage(UserRegistrationErrorMessages.FIELDS_CANNOT_BE_EMPTY.getUserText());
            return new ResponseEntity<>(userRegistrationData, HttpStatus.BAD_REQUEST);
        }
        if (isUsernameAlreadExists(userDto.getUserName())) {
            userRegistrationData.setUserName(userDto.getUserName());
            userRegistrationData.setEmail(userDto.getEmail());
            userRegistrationData.setErrorMessage(UserRegistrationErrorMessages.USERNAME_ALREADY_EXIST.getUserText());
            return new ResponseEntity<>(userRegistrationData, HttpStatus.CONFLICT);
        }
        if (isEmailAlreadyExists(userDto.getEmail())) {
            userRegistrationData.setUserName(userDto.getUserName());
            userRegistrationData.setEmail(userDto.getEmail());
            userRegistrationData.setErrorMessage(UserRegistrationErrorMessages.EMAIL_ALREADY_EXISTS.getUserText());
            return new ResponseEntity<>(userRegistrationData, HttpStatus.CONFLICT);
        }
        User user = UserMapper.userDtoToUser(userDto);
        UserProfile userProfile = new UserProfile();
        userProfile.setUser(user);
        userProfile.setTotalMoney(GameConstants.STARTING_MONEY);
        userProfileRepository.save(userProfile);
        user.setUserProfile(userProfile);
        userRepository.save(user);

        userRegistrationData.setEmail(userDto.getEmail());
        userRegistrationData.setUserName(userDto.getUserName());

        return new ResponseEntity<>(userRegistrationData, HttpStatus.CREATED);
    }

    private boolean checkPropertiesOfUserNullOrEmpty(UserDto user) {
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
        if (user.getRecoveryQuestionId() == null || user.getRecoveryQuestionId() <= 0 || user.getRecoveryQuestionId() > 10) {
            return true;
        }
        if (StringUtils.isEmpty(user.getRecoveryAnswer())) {
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

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    public ResponseEntity<UserInformationDto> getUserProfile(String authorizationHeader) throws InvalidAuthException {
        try {
            String username = extractTokenAndGetUsername(authorizationHeader);

            UserInformationDto userInformationDto = getUserByUsername(username);
            if (userInformationDto == null) {
                throw new InvalidAuthException(HttpStatus.UNAUTHORIZED, UserRegistrationErrorMessages.USER_NOT_EXISTS);
            }
            return ResponseEntity.ok(userInformationDto);

        } catch (InvalidAuthException e) {
            UserInformationDto userInformationDto = new UserInformationDto();
            userInformationDto.setErrorMessage(e.getUserRegistrationErrorMessages().getUserText());
            return new ResponseEntity<>(userInformationDto, e.getHttpStatus());
        } catch (Exception e) {
            UserInformationDto userInformationDto = new UserInformationDto();
            userInformationDto.setErrorMessage(e.getMessage());
            return new ResponseEntity<>(userInformationDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public String extractTokenAndGetUsername(String authorizationHeader) throws InvalidAuthException {
        if (!isAuthValid(authorizationHeader)) {
            throw new InvalidAuthException(HttpStatus.UNAUTHORIZED, UserRegistrationErrorMessages.INVALID_AUTH_PARAMETERS);
        }
        String token = authorizationHeader.substring(7);
        return getUsernameFromToken(token);
    }

    private boolean isAuthValid(String authorizationHeader) {
        // should verify token
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return false;
        }
        String token = authorizationHeader.substring(7); // "Bearer " prefixini kaldırıyoruz
        // verify token:
        if (!validateToken(token)) {
            return false;
        }
        return true;
    }

    private boolean validateToken(String token) {
        try {
            String secretKey = SecurityConstants.SECRET;

            // resolve token and parse it to Claims
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                    .parseClaimsJws(token)
                    .getBody();

            Date expirationDate = claims.getExpiration();

            Date now = new Date();
            // check the token's expiration
            if (expirationDate.before(now)) {
                return false;
            }
            return true;
        } catch (Exception e) {
            // invalid token
            System.out.println(e.getMessage());
            return false;
        }
    }

    // extracts the username in token
    private String getUsernameFromToken(String token) {
        try {
            String secretKey = SecurityConstants.SECRET;

            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                    .parseClaimsJws(token)
                    .getBody();

            // get username from Claims object
            String username = claims.getSubject();
            return username;
        } catch (Exception e) {
            return null;
        }
    }

    private UserInformationDto getUserByUsername(String username) {
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

    public ArrayList<String> getRecoveryQuestion(String authorizationHeader) {
        ArrayList<String> questions = new ArrayList<>();
        try {
            String username = extractTokenAndGetUsername(authorizationHeader);
            User user = userRepository.findByUserName(username);
            questions.add(RecoveryQuestion.getRecoveryQuestionById(user.getRecoveryQuestionId()));
            return questions;
        } catch (InvalidAuthException e) {
            for (RecoveryQuestion question : RecoveryQuestion.values()) {
                questions.add(question.getQuestion());
            }
            return questions;
        }
    }

    public ResponseEntity<UserInformationDto> changePassword(UserDto userDto){
        try {
            checkIfInformationsOfUserNotCorrect(userDto);
            changeWithNewPassword(userDto);

            UserInformationDto userInformationDto = new UserInformationDto();
            userInformationDto.setSuccessMessage("Password successfully changed");
            userInformationDto.setUserName(userDto.getUserName());
            userInformationDto.setEmail(userDto.getEmail());
            return new ResponseEntity<>(userInformationDto, HttpStatus.OK);

        } catch (ChangePasswordException e) {
            UserInformationDto userInformationDto = new UserInformationDto();
            userInformationDto.setErrorMessage(e.getUserRegistrationErrorMessages().getUserText());
            return new ResponseEntity<>(userInformationDto, e.getHttpStatus());
        } catch (Exception e) {
            UserInformationDto userInformationDto = new UserInformationDto();
            userInformationDto.setErrorMessage("Something went wrong while changing password.");
            return new ResponseEntity<>(userInformationDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    private void checkIfInformationsOfUserNotCorrect(UserDto user) throws ChangePasswordException {
        User createdUser = userRepository.findByUserName(user.getUserName());
        if (createdUser == null) {
            throw new ChangePasswordException(HttpStatus.NOT_FOUND, UserRegistrationErrorMessages.USER_NOT_EXISTS);
        }
        if (createdUser.getRecoveryQuestionId() != user.getRecoveryQuestionId()) {
            throw new ChangePasswordException(HttpStatus.BAD_REQUEST, UserRegistrationErrorMessages.INVALID_RECOVERY_QUESTION_FOR_SPECIFIED_USER);
        }
        if (!passwordEncoder.matches(user.getRecoveryAnswer(),createdUser.getRecoveryAnswer())){
            throw new ChangePasswordException(HttpStatus.BAD_REQUEST, UserRegistrationErrorMessages.INVALID_RECOVERY_ANSWER);
        }
        if (!createdUser.getUserName().equals(user.getUserName())) {
            throw new ChangePasswordException(HttpStatus.NOT_FOUND, UserRegistrationErrorMessages.USER_NOT_EXISTS);
        }
        if (!createdUser.getEmail().equals(user.getEmail())) {
            throw new ChangePasswordException(HttpStatus.NOT_FOUND, UserRegistrationErrorMessages.USER_NOT_EXISTS);
        }
    }
    public boolean isPasswordMatched (String password, String username) {
        return passwordEncoder.matches(password,userRepository.findByUserName(username).getPassword());
    }
    private void changeWithNewPassword (UserDto user) {
        userRepository.save(UserMapper.userDtoToUser(user));
        System.out.println("password successfully changed");
    }

    public ResponseEntity<?> getLeaderboardForMoney(String authorizationHeader) {
        try {
            extractTokenAndGetUsername(authorizationHeader);
            List<UserProfile> profiles = userProfileRepository.findAllByOrderByTotalMoneyDesc();

            ArrayList<LeaderboardDto> leaderboard = (ArrayList<LeaderboardDto>) profiles.stream()
                    .map(profile -> new LeaderboardDto(profile.getUser().getUserName(), profile.getTotalMoney()))
                    .collect(Collectors.toList());
            return new ResponseEntity<>(leaderboard, HttpStatus.OK);

        } catch (InvalidAuthException e) {
            UserInformationDto userInformationDto = new UserInformationDto();
            userInformationDto.setErrorMessage(e.getUserRegistrationErrorMessages().getUserText());
            return new ResponseEntity<>(userInformationDto, e.getHttpStatus());
        } catch (Exception e) {
            UserInformationDto userInformationDto = new UserInformationDto();
            userInformationDto.setErrorMessage(e.getMessage());
            return new ResponseEntity<>(userInformationDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    public ResponseEntity<String> loginUser(LoginForm loginForm) {
        final String username = loginForm.getUsername();
        final String password = loginForm.getPassword();
        if (!isPasswordMatched(password, username)) {
            return new ResponseEntity<>(UserRegistrationErrorMessages.INVALID_AUTH_PARAMETERS.getUserText(), HttpStatus.BAD_REQUEST);

        }
        // verify username and password of user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // create JWT and return it
        String token = Jwts.builder()
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.SECRET.getBytes())
                .compact();
        return ResponseEntity.ok(token);
    }
}
