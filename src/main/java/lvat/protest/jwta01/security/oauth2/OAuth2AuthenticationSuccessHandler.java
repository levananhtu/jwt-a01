package lvat.protest.jwta01.security.oauth2;

import lvat.protest.jwta01.exception.BadRequestException;
import lvat.protest.jwta01.security.UserPrincipal;
import lvat.protest.jwta01.security.token_provider.AccessTokenProvider;
import lvat.protest.jwta01.util.CookieUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;

import static lvat.protest.jwta01.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URL_PARAM_COOKIE_NAME;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final AccessTokenProvider accessTokenProvider;
    private final HttpCookieOAuth2AuthorizationRequestRepository cookieOAuth2AuthorizationRequestRepository;

    @Value(value = "${app.oauth2.authorizedRedirectUris}")
    private String redirectUri;

    public OAuth2AuthenticationSuccessHandler(AccessTokenProvider accessTokenProvider, HttpCookieOAuth2AuthorizationRequestRepository cookieOAuth2AuthorizationRequestRepository) {
        this.accessTokenProvider = accessTokenProvider;
        this.cookieOAuth2AuthorizationRequestRepository = cookieOAuth2AuthorizationRequestRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String targetUrl = determineTargetUrl(request, response, authentication);

        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Optional<String> redirectUri = CookieUtil.getCookie(request, REDIRECT_URL_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);

        if (redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
            throw new BadRequestException("Sorry! We've got an Unauthorized Redirect URI and can't proceed with the authentication");
        }

        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());

        String token = accessTokenProvider.generateAccessToken(((UserPrincipal) authentication.getPrincipal()).getPublicUserId());

        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("token", token)
                .build().toUriString();
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        cookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookie(request, response);
    }


    private boolean isAuthorizedRedirectUri(String uri) {
        URI clientRedirectUri = URI.create(uri);
        URI authorizedRedirectUri = URI.create(redirectUri);
        return authorizedRedirectUri.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
                && authorizedRedirectUri.getPort() == clientRedirectUri.getPort();
    }
}
