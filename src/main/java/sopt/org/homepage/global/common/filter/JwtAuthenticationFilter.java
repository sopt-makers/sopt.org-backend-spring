package sopt.org.homepage.global.common.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import sopt.org.homepage.global.common.jwt.JwtTokenProvider;
import sopt.org.homepage.global.exception.TokenException;


@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        val uri = request.getRequestURI();

        if (uri.startsWith("/v2/admin")) {
            val token = jwtTokenProvider.resolveToken(request);

            checkJwtAvailable(token);

            val auth = jwtTokenProvider.getAuthentication(token);
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        chain.doFilter(request, response);
    }

    private void checkJwtAvailable(String token) {
        if (token == null || !jwtTokenProvider.validateTokenExpiration(token)) {
            throw new TokenException("유효하지 않은 토큰입니다.");
        }
    }
}
