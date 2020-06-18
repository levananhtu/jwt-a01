package lvat.protest.jwta01.controller;

import lvat.protest.jwta01.entity.User;
import lvat.protest.jwta01.exception.UserConflictException;
import lvat.protest.jwta01.exception.UserNotFoundException;
import lvat.protest.jwta01.exception_handle.ApplicationExceptionHandle;
import lvat.protest.jwta01.mailer.MailService;
import lvat.protest.jwta01.payload.*;
import lvat.protest.jwta01.repository.RedisRepository;
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
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/auth")
public class AuthController extends ApplicationExceptionHandle {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);
    private final AccessTokenProvider accessTokenProvider;
    private final RefreshTokenProvider refreshTokenProvider;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final MailService mailService;
    private final RedisRepository redisRepository;

    public AuthController(AccessTokenProvider accessTokenProvider, UserService userService, RefreshTokenProvider refreshTokenProvider, AuthenticationManager authenticationManager, MailService mailService, RedisRepository redisRepository) {
        this.accessTokenProvider = accessTokenProvider;
        this.userService = userService;
        this.refreshTokenProvider = refreshTokenProvider;
        this.authenticationManager = authenticationManager;
        this.mailService = mailService;
        this.redisRepository = redisRepository;
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
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginPayload.getEmailOrUsername(),
                        loginPayload.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return new ResponseEntity<>(
                new TokenPairPayload(
                        accessTokenProvider.generateAccessToken(authentication),
                        refreshTokenProvider.generateRefreshToken(authentication)),
                HttpStatus.OK);
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

    @RequestMapping(path = "/forgot-password", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> payload) {
        String emailOrUsername = payload.get("emailOrUsername");
        try {
            User user = userService.findByUsernameOrEmail(emailOrUsername, emailOrUsername);
            String resetCode = UUID.randomUUID().toString().replace("-", "");
            String resetPasswordLink = "http://localhost:3000/reset-password?rc=" + resetCode;
            redisRepository.addResetPasswordCode(resetCode, user.getPublicUserId());
            try {
                mailService.sendEmail(new String[]{user.getEmail()}, "about password reset request", "<a href=\"" + resetPasswordLink + "\">link</a>");
            } catch (MessagingException e) {
                LOGGER.info("unknown password reset request");
            }

        } catch (UserNotFoundException e) {
            LOGGER.info("unknown password reset request");
        }
        return new ResponseEntity<>(
                new MessagePayload("mail sent!!"),
                HttpStatus.OK);
    }

    @RequestMapping(path = "/reset-password", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordPayload resetPasswordPayload) {
        String resetCode = resetPasswordPayload.getRc();
        String password = resetPasswordPayload.getPassword();
        if (!password.equals(resetPasswordPayload.getConfirmedPassword())) {
            return new ResponseEntity<>(new MessagePayload("password and password confirm must be identical!!"), HttpStatus.BAD_REQUEST);
        }
        String publicUserId = redisRepository.getPublicUserIdFromResetPasswordCode(resetCode);
        if (publicUserId == null) {
            return new ResponseEntity<>(new MessagePayload("deny"), HttpStatus.BAD_REQUEST);
        }
        redisRepository.removeResetPasswordCode(resetCode);
        try {
            userService.updatePassword(publicUserId, password);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(new MessagePayload("deny"), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new MessagePayload("password updated!!"), HttpStatus.OK);
    }

    @RequestMapping(path = "/check-reset-password", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> checkResetPassword(@RequestBody Map<String, String> body) {
        String resetCode = body.get("rc");
        if (redisRepository.getPublicUserIdFromResetPasswordCode(resetCode) == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    @RequestMapping(path = "/abort-reset-password", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> abortResetPassword(@RequestParam(name = "rc") String resetCode) {
        redisRepository.removeResetPasswordCode(resetCode);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(path = "/change-password", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordPayload changePasswordPayload, @RequestHeader(name = "Authorization") String authorizationHeader) {
        if (!ChangePasswordPayload.doesNewPasswordEqualConfirmedNewPassword(changePasswordPayload)) {
            return new ResponseEntity<>(new MessagePayload("password error"), HttpStatus.BAD_REQUEST);
        }
        String accessToken = authorizationHeader.substring("Bearer ".length());
        String publicUserId = accessTokenProvider.getPublicUserIdFromAccessToken(accessToken);
        try {
            userService.updatePassword(publicUserId, changePasswordPayload.getNewPassword());
        } catch (UserNotFoundException ignored) {

        }
        return new ResponseEntity<>(new MessagePayload("password updated successfully"), HttpStatus.OK);
    }

    @RequestMapping(path = "/ping", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> ping(@RequestBody Map<String, String> body) {
        String accessToken = body.get("accessToken");
        if (accessTokenProvider.isValidAccessToken(accessToken)) {
            String publicUserId = accessTokenProvider.getPublicUserIdFromAccessToken(accessToken);
            try {
                userService.findByPublicUserId(publicUserId);
                return ResponseEntity.ok().build();
            } catch (UserNotFoundException e) {
                return ResponseEntity.badRequest().build();
            }
        }
        return ResponseEntity.badRequest().build();
    }
}