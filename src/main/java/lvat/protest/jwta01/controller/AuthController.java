package lvat.protest.jwta01.controller;

import lvat.protest.jwta01.entity.User;
import lvat.protest.jwta01.exception.UserConflictException;
import lvat.protest.jwta01.exception.UserNotFoundException;
import lvat.protest.jwta01.exception_handle.ApplicationExceptionHandle;
import lvat.protest.jwta01.payload.*;
import lvat.protest.jwta01.security.token_provider.AccessTokenProvider;
import lvat.protest.jwta01.security.token_provider.RefreshTokenProvider;
import lvat.protest.jwta01.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/auth")
public class AuthController extends ApplicationExceptionHandle {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);
    private final AccessTokenProvider accessTokenProvider;
    private final RefreshTokenProvider refreshTokenProvider;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    public AuthController(AccessTokenProvider accessTokenProvider, UserService userService, RefreshTokenProvider refreshTokenProvider, AuthenticationManager authenticationManager) {
        this.accessTokenProvider = accessTokenProvider;
        this.userService = userService;
        this.refreshTokenProvider = refreshTokenProvider;
        this.authenticationManager = authenticationManager;
    }

    @RequestMapping(path = "/sign-up", method = RequestMethod.POST)
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpPayload signUpPayload) {
        try {
            userService.createNewUser(signUpPayload.getName(),
                    signUpPayload.getUsername(),
                    signUpPayload.getEmail(),
                    signUpPayload.getPassword());
        } catch (UserConflictException e) {
            return new ResponseEntity<>(
                    new MessagePayload("Account with username or email with given detail has already exists"),
                    HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(
                new MessagePayload("Account created successfully"),
                HttpStatus.CREATED);
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public ResponseEntity<?> login(@Valid @RequestBody LoginPayload loginPayload) {
        try {
            User user = userService.findByCredentials(loginPayload.getEmailOrUsername(), loginPayload.getPassword());
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginPayload.getEmailOrUsername(),
                            loginPayload.getPassword()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return new ResponseEntity<>(
                    new TokenPairPayload(
                            accessTokenProvider.generateAccessToken(user.getPublicUserId()),
                            refreshTokenProvider.generateRefreshToken(user.getPublicUserId())),
                    HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(
                    new MessagePayload("Username, email or password is not valid"),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(path = "/logout", method = RequestMethod.POST)
    public ResponseEntity<?> logOut(@Valid @RequestBody TokenPairPayload tokenPairPayload) {
        String accessToken = tokenPairPayload.getAccessToken();
        String refreshToken = tokenPairPayload.getRefreshToken();
        accessTokenProvider.removeAccessToken(accessToken);
        refreshTokenProvider.removeRefreshToken(refreshToken);
        return new ResponseEntity<>(
                new MessagePayload("Logout successfully"),
                HttpStatus.OK);

    }

//    @RequestMapping(path = "/promote-user", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<?> promoteUser(@RequestBody Map<String, String> publicUserIdPayload) {
//        if (userService.promoteUser(publicUserIdPayload.get("publicUserId"))) {
//            return new ResponseEntity<>(new MessagePayload("Account promote to role ADMIN successfully"),
//                    HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>(new MessagePayload("Account promote to role admin unsuccessfully"),
//                    HttpStatus.BAD_REQUEST);
//        }
//    }
//
//    @RequestMapping(path = "/demote-user", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<?> demoteUser(@RequestBody Map<String, String> publicUserIdPayload) {
//        if (userService.demoteUser(publicUserIdPayload.get("publicUserId"))) {
//            return new ResponseEntity<>(new MessagePayload("Account demote to role USER successfully"),
//                    HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>(new MessagePayload("Account demote to role user unsuccessfully"),
//                    HttpStatus.BAD_REQUEST);
//        }
//    }

    @RequestMapping(path = "/forgot-password", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> publicUserIdPayload) {
        return null;
    }

    @RequestMapping(path = "/reset-password", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> resetPassword(@RequestBody ChangePasswordPayload changePasswordPayload) {
        return null;
    }
}
