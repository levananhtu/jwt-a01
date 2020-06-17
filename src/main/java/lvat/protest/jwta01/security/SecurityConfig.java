package lvat.protest.jwta01.security;

import lvat.protest.jwta01.entity.Role;
import lvat.protest.jwta01.security.oauth2.CustomOAuth2UserService;
import lvat.protest.jwta01.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import lvat.protest.jwta01.security.oauth2.OAuth2AuthenticationFailureHandler;
import lvat.protest.jwta01.security.oauth2.OAuth2AuthenticationSuccessHandler;
import lvat.protest.jwta01.security.token_provider.AccessTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true,
        prePostEnabled = true,
        jsr250Enabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final ApplicationContext applicationContext;
    private final UserPrincipalService userPrincipalService;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
private final HttpCookieOAuth2AuthorizationRequestRepository cookieOAuth2AuthorizationRequestRepository;
    public SecurityConfig(ApplicationContext applicationContext, UserPrincipalService userPrincipalService, JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint, CustomOAuth2UserService customOAuth2UserService, OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler, OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler, HttpCookieOAuth2AuthorizationRequestRepository cookieOAuth2AuthorizationRequestRepository) {
        this.applicationContext = applicationContext;
        this.userPrincipalService = userPrincipalService;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.customOAuth2UserService = customOAuth2UserService;
        this.oAuth2AuthenticationSuccessHandler = oAuth2AuthenticationSuccessHandler;
        this.oAuth2AuthenticationFailureHandler = oAuth2AuthenticationFailureHandler;
        this.cookieOAuth2AuthorizationRequestRepository = cookieOAuth2AuthorizationRequestRepository;
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(applicationContext.getBean(AccessTokenProvider.class),
                applicationContext.getBean(UserPrincipalService.class));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean(value = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userPrincipalService)
                .passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        System.out.println("abcxyz" + Role.RoleName.ROLE_ADMIN.getRole());
        http.csrf().disable();
        http.cors();
        http.exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint);
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeRequests()
                .antMatchers(HttpMethod.POST, "/api/auth/sign-up")
                .hasAuthority(Role.RoleName.ROLE_ADMIN.getRole())
                .antMatchers(HttpMethod.POST, "/api/auth/login")
                .not().authenticated()
//                .permitAll()
                .antMatchers(HttpMethod.PUT, "/api/auth/promote-user")
                .hasAuthority(Role.RoleName.ROLE_ADMIN.getRole())
//                .authenticated()
//                .permitAll()
                .antMatchers(HttpMethod.PUT, "/api/auth/demote-user")
                .hasAuthority(Role.RoleName.ROLE_ADMIN.getRole())
                .antMatchers(HttpMethod.GET, "/test/api/create-test")
                .permitAll()
//                .authenticated();
//                .permitAll()
                .anyRequest()
                .authenticated();
        http.oauth2Login()
                .authorizationEndpoint()
                .baseUri("/oauth2/authorize")
                .authorizationRequestRepository(cookieOAuth2AuthorizationRequestRepository)
                .and()
                .redirectionEndpoint()
                .baseUri("/oauth2/callback/*")
                .and()
                .userInfoEndpoint()
                .userService(customOAuth2UserService)
                .and()
                .successHandler(oAuth2AuthenticationSuccessHandler)
                .failureHandler(oAuth2AuthenticationFailureHandler);

        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }
}
