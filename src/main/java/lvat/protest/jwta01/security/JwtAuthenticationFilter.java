package lvat.protest.jwta01.security;

import lvat.protest.jwta01.security.token_provider.AccessTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final AccessTokenProvider accessTokenProvider;
    private final UserPrincipalService userPrincipalService;

    public JwtAuthenticationFilter(AccessTokenProvider accessTokenProvider, UserPrincipalService userPrincipalService) {
        this.accessTokenProvider = accessTokenProvider;
        this.userPrincipalService = userPrincipalService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        try {
            String accessToken = getAccessTokenFromRequest(httpServletRequest);
            if (StringUtils.hasText(accessToken) && accessTokenProvider.isValidAccessToken(accessToken)) {
                String publicUserId = accessTokenProvider.getPublicUserIdFromAccessToken(accessToken);
                UserDetails userDetails = userPrincipalService.loadUserByPublicUserId(publicUserId);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            LOGGER.error("Could not set user authentication in security context", e);
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private String getAccessTokenFromRequest(HttpServletRequest httpServletRequest) {
        String bearerToken = httpServletRequest.getHeader("Authorization");
        String tokenPrefixInHeader = "Bearer ";
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(tokenPrefixInHeader)) {
            return bearerToken.substring(tokenPrefixInHeader.length());
        }
        return null;
    }
}
