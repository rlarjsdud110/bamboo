package bamboo.service;

import bamboo.config.jwt.TokenProvider;
import bamboo.entity.TokenEntity;
import bamboo.entity.UserEntity;
import bamboo.exception.CustomException;
import bamboo.exception.ErrorCode;
import bamboo.repository.TokenRepository;
import bamboo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RequiredArgsConstructor
@Service
public class TokenService {

    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    public String createAccessToken(String refreshToken){
        log.info("[createAccessToken] 실행");

        if(!tokenProvider.validationToken(refreshToken)){
            throw new CustomException("토큰이 존재하지 않습니다.", ErrorCode.TOKEN_NOT_FOUND);
        }

        TokenEntity token = tokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new CustomException("토큰이 존재하지 않습니다.", ErrorCode.TOKEN_NOT_FOUND));

        UserEntity user = userRepository.findById(tokenProvider.getUserNo(token.getRefreshToken()))
                .orElseThrow(() -> new CustomException("다시 시도해 주세요.", ErrorCode.RETRY));

        return tokenProvider.accessToken(user, token);
    }


}
