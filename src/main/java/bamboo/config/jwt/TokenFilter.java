package bamboo.config.jwt;

import bamboo.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class TokenFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;
    private final String HEADER_AUTHORIZATION = "X-AUTH-TOKEN";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        log.info("[doFilterInternal] 실행");
        String authorizationHeader = request.getHeader(HEADER_AUTHORIZATION);

        if (authorizationHeader != null) {
            String token = authorizationHeader.trim();

            try {
                if (tokenProvider.validationToken(token)) {
                    Authentication authentication = tokenProvider.getAuthentication(token);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (CustomException e){
                response.setStatus(e.getHttpStatus().value());
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("세션이 만료되었습니다.");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}

