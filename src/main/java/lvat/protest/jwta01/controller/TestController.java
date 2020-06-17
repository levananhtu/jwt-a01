package lvat.protest.jwta01.controller;

import lvat.protest.jwta01.entity.User;
import lvat.protest.jwta01.exception.UserConflictException;
import lvat.protest.jwta01.exception.UserNotFoundException;
import lvat.protest.jwta01.mailer.MailService;
import lvat.protest.jwta01.payload.LoginPayload;
import lvat.protest.jwta01.payload.SignUpPayload;
import lvat.protest.jwta01.repository.RedisRepository;
import lvat.protest.jwta01.security.token_provider.AccessTokenProvider;
import lvat.protest.jwta01.security.token_provider.RefreshTokenProvider;
import lvat.protest.jwta01.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.util.List;

@RestController
@RequestMapping(path = "/test/api/")
public class TestController {
    private final UserService userService;
    private final AccessTokenProvider accessTokenProvider;
    private final RefreshTokenProvider refreshTokenProvider;
    private final RedisRepository redisRepository;
    private final MailService mailSender;

    public TestController(UserService userService, AccessTokenProvider accessTokenProvider, RefreshTokenProvider refreshTokenProvider, RedisRepository redisRepository, MailService mailSender) {
        this.userService = userService;
        this.accessTokenProvider = accessTokenProvider;
        this.refreshTokenProvider = refreshTokenProvider;
        this.redisRepository = redisRepository;
        this.mailSender = mailSender;
    }

    @RequestMapping(path = "/redis/set", method = RequestMethod.GET)
    public void set(@RequestParam(name = "key") String key, @RequestParam(name = "value") Boolean value) {
        redisRepository.set(key, value);
    }

    @RequestMapping(path = "/redis/get", method = RequestMethod.GET)
    public Boolean get(@RequestParam(name = "key") String key) {
        return redisRepository.get(key) == null ? false : redisRepository.get(key);
    }

    @RequestMapping(path = "/redis/get-expire", method = RequestMethod.GET)
    public Long getExpire(@RequestParam(name = "key") String key) {
        return redisRepository.getExpire(key);
    }

    @RequestMapping(path = "create-test", method = RequestMethod.GET)
    public List<User> createTest() {
        return userService.createTest();
    }

    @RequestMapping(path = "sign-in", method = RequestMethod.POST)
    public String signIn(@RequestBody LoginPayload loginPayload) throws UserNotFoundException {
////    public ResponseEntity<? extends PayloadResponse> signIn(@RequestBody SignInPayload signInPayload) {
        User user = userService.findByCredentials(loginPayload.getEmailOrUsername(), loginPayload.getPassword());
        return accessTokenProvider.generateAccessToken(user.getPublicUserId().toString());
    }


    @RequestMapping(path = "sign-up", method = RequestMethod.POST)
    public User signUp(@RequestBody SignUpPayload signUpPayload) throws UserConflictException {
        return userService.createNewUser(signUpPayload.getName(), signUpPayload.getUsername(), signUpPayload.getEmail(), signUpPayload.getPassword());
    }

    @RequestMapping(path = "remove-access-token", method = RequestMethod.GET)
    public Boolean removeAccessToken(@RequestParam(name = "access-token") String accessToken) {
        return accessTokenProvider.removeAccessToken(accessToken);
    }

    @RequestMapping(path = "promote", method = RequestMethod.GET)
    public Boolean promote(@RequestParam(name = "public-user-id") String publicUserId) {
        return userService.promoteUser(publicUserId);
    }

    @RequestMapping(path = "demote", method = RequestMethod.GET)
    public Boolean demote(@RequestParam(name = "public-user-id") String publicUserId) {
        return userService.demoteUser(publicUserId);
    }

    @RequestMapping(path = "check-access", method = RequestMethod.GET)
    public Boolean checkAccess(@RequestParam(name = "token", defaultValue = "") String token) {
        return accessTokenProvider.isValidAccessToken(token);
    }

    @RequestMapping(path = "check-refresh", method = RequestMethod.GET)
    public Boolean checkRefresh(@RequestParam(name = "token", defaultValue = "") String token) {
        return refreshTokenProvider.isValidRefreshToken(token);
    }

    @RequestMapping(path = "mail")
    public void mail() throws MessagingException {
        mailSender.d();
    }

}
