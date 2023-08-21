package com.architecture.springboot.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.slf4j.MDC;
import org.slf4j.helpers.BasicMDCAdapter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws ServletException, IOException {
        try {
            // TODO MDC FIX
            final String traceId = UUID.randomUUID().toString();
            log.info("traceId : {}", traceId);
            MDC.put("traceId", traceId);
            if (isAsyncDispatch(request)) {
                filterChain.doFilter(request, response);
            } else {
                doFilterWrapped(new RequestWrapper(request), new ResponseWrapper(response), filterChain);
            }
        } finally {
            log.info("mdc value : {}", MDC.get("traceId"));
            MDC.clear();
        }
    }

    protected void doFilterWrapped(RequestWrapper request, ContentCachingResponseWrapper response, FilterChain filterChain) throws ServletException, IOException {
        boolean isAPI = request.getRequestURI().contains("/api/");
        try {
            if (isAPI) logRequest(request);
            filterChain.doFilter(request, response);
        } finally {
            if (isAPI) logResponse(response);
            response.copyBodyToResponse();
        }
    }

    private static void logRequest(RequestWrapper request) throws IOException {
        String queryString = request.getQueryString();
        log.info("Request : {} uri=[{}] content-type=[{}]",
                request.getMethod(),
                queryString == null ? request.getRequestURI() : request.getRequestURI() + queryString,
                request.getContentType()
        );

        log.info("Request Headers : {}",
                analyzeHeader(request)
        );
        logPayload("Request", request.getContentType(), request.getInputStream());
    }

    private static void logResponse(ContentCachingResponseWrapper response) throws IOException {
        logPayload("Response", response.getContentType(), response.getContentInputStream());
    }

    private static void logPayload(String prefix, String contentType, InputStream inputStream) throws IOException {
        boolean visible = isVisible(MediaType.valueOf(contentType == null ? "application/json" : contentType));
        if (visible) {
            byte[] content = StreamUtils.copyToByteArray(inputStream);
            if (content.length > 0) {
                String contentString = new String(content);
                log.info("{} Payload: {}", prefix, contentString);
            }
        } else {
            log.info("{} Payload: Binary Content", prefix);
        }
    }

    private static boolean isVisible(MediaType mediaType) {
        final List<MediaType> VISIBLE_TYPES = List.of(
                MediaType.valueOf("text/*"),
                MediaType.APPLICATION_FORM_URLENCODED,
                MediaType.APPLICATION_JSON,
                MediaType.APPLICATION_XML,
                MediaType.valueOf("application/*+json"),
                MediaType.valueOf("application/*+xml"),
                MediaType.MULTIPART_FORM_DATA
        );

        return VISIBLE_TYPES.stream()
                .anyMatch(visibleType -> visibleType.includes(mediaType));
    }

    private static Map<String, String> analyzeHeader(RequestWrapper request) {
        return Collections.list(request.getHeaderNames())
                .stream()
                .collect(Collectors.toMap(
                        name -> name,
                        request::getHeader));
    }
}
