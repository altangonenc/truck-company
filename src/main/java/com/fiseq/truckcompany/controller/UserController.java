package com.fiseq.truckcompany.controller;

import com.fiseq.truckcompany.constants.SecurityConstants;
import com.fiseq.truckcompany.dto.LoginForm;
import com.fiseq.truckcompany.dto.UserInformationDto;
import com.fiseq.truckcompany.dto.UserRegistrationData;
import com.fiseq.truckcompany.entities.User;
import com.fiseq.truckcompany.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserController(UserService userService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<UserRegistrationData> registerUser(@RequestBody User user) {
        return userService.registerUser(user);
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody LoginForm loginForm) {
        final String username = loginForm.getUsername();
        final String password = loginForm.getPassword();
        // Kimlik doğrulama işlemi
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // JWT oluşturma ve tokeni döndürme
        String token = Jwts.builder()
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.SECRET.getBytes())
                .compact();
        return ResponseEntity.ok(token);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserInformationDto> getUserProfile(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            // Token doğrulaması yapılması gerekiyor
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                UserInformationDto userInformationDtoError = new UserInformationDto();
                userInformationDtoError.setErrorMessage("Invalid authorization header");
                return new ResponseEntity<>(userInformationDtoError, HttpStatus.UNAUTHORIZED);
            }

            String token = authorizationHeader.substring(7); // "Bearer " prefixini kaldırıyoruz
            // Token doğrulama işlemini gerçekleştirin, örnek olarak:
            if (!validateToken(token)) {
                UserInformationDto userInformationDtoError = new UserInformationDto();
                userInformationDtoError.setErrorMessage("Invalid token");
                return new ResponseEntity<>(userInformationDtoError, HttpStatus.UNAUTHORIZED);
            }

            // Token doğrulandı, kullanıcı profilini döndürün
            String username = getUsernameFromToken(token);
            UserInformationDto userInformationDto = userService.getUserByUsername(username);
            if (userInformationDto == null) {
                UserInformationDto userInformationDtoError = new UserInformationDto();
                userInformationDtoError.setErrorMessage("User does not exist");
                return new ResponseEntity<>(userInformationDtoError, HttpStatus.BAD_REQUEST);
            }
            return ResponseEntity.ok(userInformationDto);
        } catch (Exception e) {
            // İstisna durumunda hata mesajı döndürün
            UserInformationDto userInformationDtoError = new UserInformationDto();
            userInformationDtoError.setErrorMessage(e.getMessage());
            return new ResponseEntity<>(userInformationDtoError, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Token doğrulama işlemini gerçekleştiren metot
    private boolean validateToken(String token) {
        try {
            // JWT'yı doğrulamak için kullanılacak anahtar (şifre)
            String secretKey = SecurityConstants.SECRET;

            // Token'ı çözümle ve Claims nesnesine dönüştür
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                    .parseClaimsJws(token)
                    .getBody();

            // Token'ın son kullanma tarihini al
            Date expirationDate = claims.getExpiration();

            // Şu anki zamanı al
            Date now = new Date();

            // Token'ın son kullanma tarihine göre kontrol et
            if (expirationDate.before(now)) {
                // Token süresi geçmiş, geçerli değil
                return false;
            }

            // Token geçerli
            return true;
        } catch (Exception e) {
            // Token çözümleme hatası veya geçersiz token
            System.out.println(e.getMessage());
            return false;
        }
    }

    // Token'dan kullanıcı adını çıkaran metot
    private String getUsernameFromToken(String token) {
        try {
            // JWT'yı çözmek için kullanılacak anahtar (şifre)
            String secretKey = SecurityConstants.SECRET;

            // Token'ı çözümle ve Claims nesnesine dönüştür
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                    .parseClaimsJws(token)
                    .getBody();

            // Claims nesnesinden kullanıcı adını al
            String username = claims.getSubject();

            return username;
        } catch (Exception e) {
            return null;
        }
    }
}
