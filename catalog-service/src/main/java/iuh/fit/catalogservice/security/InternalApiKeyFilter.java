package iuh.fit.catalogservice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import iuh.fit.catalogservice.config.InternalApiKeyProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
public class InternalApiKeyFilter extends OncePerRequestFilter {

    private static final String INTERNAL_PREFIX = "/internal/";

    private final InternalApiKeyProperties properties;
    private final ObjectMapper objectMapper;

    public InternalApiKeyFilter(InternalApiKeyProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String path = request.getRequestURI();
        if (!path.startsWith(INTERNAL_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String headerName = properties.getHeaderName();
        String expectedKey = properties.getKey();
        if (!StringUtils.hasText(headerName) || !StringUtils.hasText(expectedKey)) {
            writeError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Internal API key not configured");
            return;
        }

        String providedKey = request.getHeader(headerName);
        if (!expectedKey.equals(providedKey)) {
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "Invalid internal API key");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void writeError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        Map<String, Object> payload = new HashMap<>();
        payload.put("status", status);
        payload.put("error", message);
        objectMapper.writeValue(response.getWriter(), payload);
    }
}
