package ru.practicum.shareit.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String httpMethod = request.getMethod();
        String uri = request.getRequestURI();
        String remoteAddress = request.getRemoteAddr();

        log.info("Получен запрос: {} {} от IP: {}", httpMethod, uri, remoteAddress);

        filterChain.doFilter(request, response);
    }
}


