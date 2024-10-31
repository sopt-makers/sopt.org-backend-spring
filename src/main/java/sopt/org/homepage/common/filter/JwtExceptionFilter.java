package sopt.org.homepage.common.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import sopt.org.homepage.exception.TokenException;

import java.io.IOException;

@Component
public class JwtExceptionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            FilterChain filterChain
    ) throws ServletException, IOException {
       try {
           filterChain.doFilter(httpServletRequest, httpServletResponse);
       } catch(TokenException e) {
           val objectMapper = new ObjectMapper();
           val jsonResponse = objectMapper.writeValueAsString(e.getMessage());

           httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
           httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
           httpServletResponse.setCharacterEncoding("UTF-8");
           httpServletResponse.getWriter().write(jsonResponse);
       }
    }

}