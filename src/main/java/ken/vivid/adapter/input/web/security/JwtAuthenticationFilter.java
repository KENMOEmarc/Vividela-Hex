package ken.vivid.adapter.input.web.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ken.vivid.application.port.output.auth.TokenBlacklist;
import ken.vivid.application.port.output.auth.TokenGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenGenerator tokenGenerator;
    private final TokenBlacklist tokenBlacklist;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        extractToken(request)
                .ifPresentOrElse(token -> {
                    if (tokenBlacklist.isRevoked(token)) {
                        log.warn("Rejected revoked JWT for request {}", request.getRequestURI());
                        return;
                    }
                    tokenGenerator.validateAndExtractEmail(token)
                            .ifPresentOrElse(
                                    email -> authenticate(email, request),
                                    () -> log.warn("Invalid JWT supplied for request {}", request.getRequestURI())
                            );
                }, () -> log.debug("No Authorization header provided for request {}", request.getRequestURI()));

        chain.doFilter(request, response);
    }

    private void authenticate(String email, HttpServletRequest request) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        var authToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
        log.debug("JWT authenticated for principal={} on {}", email, request.getRequestURI());
    }

    private Optional<String> extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return Optional.of(header.substring(7));
        }
        return Optional.empty();
    }
}
