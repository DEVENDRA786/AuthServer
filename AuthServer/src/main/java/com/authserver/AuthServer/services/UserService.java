package com.authserver.AuthServer.services;


import com.authserver.AuthServer.models.Token;
import com.authserver.AuthServer.models.User;
import com.authserver.AuthServer.repositories.TokenRepository;
import com.authserver.AuthServer.repositories.UserRepository;
import com.authserver.AuthServer.security.services.CustomUserDetailsService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

@Service
public class UserService {
    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private CustomUserDetailsService customUserDetailsService;

    private TokenRepository tokenRepository;

    public UserService(UserRepository userRepository,
                       BCryptPasswordEncoder bCryptPasswordEncoder,
                       TokenRepository tokenRepository,
                       CustomUserDetailsService customUserDetailsService) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.tokenRepository = tokenRepository;
        this.customUserDetailsService =customUserDetailsService;
    }

    public User signUp(String fullName,
                       String email,
                       String password) {
        User u = new User();
        u.setEmail(email);
        u.setName(fullName);
        u.setHashedPassword(bCryptPasswordEncoder.encode(password));

        User user = userRepository.save(u);

        return user;
    }

    public Object login(String email, String password) {
//        Optional<User> userOptional = userRepository.findByEmail(email);
//
//        if (userOptional.isEmpty()) {
//            // throw user not exists exception
//            return null;
//        }
//
//        User user = userOptional.get();

       UserDetails user = customUserDetailsService.loadUserByUsername(email);


        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("No User Password Match");
        }
//
//        Token token = getToken(user);
//
//        // TODO 1: Change the above token to a JWT Token
//
//        Token savedToken = tokenRepository.save(token);

        return user;
    }

    private static Token getToken(User user) {
        LocalDate today = LocalDate.now();
        LocalDate thirtyDaysLater = today.plus(30, ChronoUnit.DAYS);

        // Convert LocalDate to Date
        Date expiryDate = Date.from(thirtyDaysLater.atStartOfDay(ZoneId.systemDefault()).toInstant());

        Token token = new Token();
        token.setUser(user);
        token.setExpiryAt(expiryDate);
        token.setValue(RandomStringUtils.randomAlphanumeric(128));
        return token;
    }

    public void logout(String token) {
        Optional<Token> token1 = tokenRepository.findByValueAndDeletedEquals(token, false);

        if (token1.isEmpty()) {
            // throw TokenNotExistsOrAlreadyExpiredException();
            return;
        }

        Token tkn = token1.get();

        tkn.setDeleted(true);
        tokenRepository.save(tkn);

        return;

    }

    public User validateToken(String token) {
        Optional<Token> tkn = tokenRepository.
                findByValueAndDeletedEqualsAndExpiryAtGreaterThan(token, false, new Date());

        if (tkn.isEmpty()) {
            return null;
        }

        // TODO 2: Instead of validating via the DB, as the token is now a JWT
        // token, validate using JWT

        return tkn.get().getUser();
    }
}
