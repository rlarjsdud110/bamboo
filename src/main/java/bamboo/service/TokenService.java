package bamboo.service;

import bamboo.config.jwt.TokenProvider;
import bamboo.entity.TokenEntity;
import bamboo.entity.UserEntity;
import bamboo.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RequiredArgsConstructor
@Service
public class TokenService {

    private final TokenProvider tokenProvider;
    private final UserService userService;
    private final TokenRepository tokenRepository;

    public String createAccessToken(HttpServletRequest request){
        log.info("[createAccessToken] 실행");
        String refreshToken = request.getHeader("R-AUTH-TOKEN");

        if(!tokenProvider.validationToken(refreshToken)){
            throw new IllegalArgumentException("Unexpected Token Validation");
        }

        TokenEntity key = tokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected token"));

        UserEntity user = userService.findById(tokenProvider.getUserNo(key.getRefreshToken()));

        return tokenProvider.accessToken(user, key);
    }

    public void logout(HttpServletRequest request){
        log.info("[tokenProvider.delete] 실행");
        String refreshToken = request.getHeader("R-AUTH-TOKEN");
        if(!tokenProvider.validationToken(refreshToken)){
            throw new IllegalArgumentException("Unexpected token");
        }
        TokenEntity key = tokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected token"));

        tokenProvider.deleteToken(key);
    }
}
