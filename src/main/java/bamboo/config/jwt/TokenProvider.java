package bamboo.config.jwt;

import bamboo.dto.TokenDTO;
import bamboo.entity.TokenEntity;
import bamboo.entity.UserEntity;
import bamboo.exception.CustomException;
import bamboo.repository.TokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
public class TokenProvider {

    private final JwtProperties jwtProperties;
    private final TokenRepository tokenRepository;
    public static final Long REFRESH_TOKEN_DURATION = 1000L*60*60*24*3;
    public static final Long ACCESS_TOKEN_DURATION = 1000L*60*60*3;

    public TokenDTO createToken(UserEntity user){
        log.info("[createToken] 실행");

        String accessToken = makeToken(ACCESS_TOKEN_DURATION, user);
        String refreshToken = makeToken(REFRESH_TOKEN_DURATION, user);

        tokenRepository.save(TokenEntity.builder()
                .userNo(user.getUserNo())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiration(REFRESH_TOKEN_DURATION)
                .build());

        return TokenDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
    public String accessToken(UserEntity user, TokenEntity token){
        log.info("[accessToken] 실행");

        String accessToken = makeToken(ACCESS_TOKEN_DURATION, user);

        token.setAccessToken(accessToken);

        tokenRepository.save(token);

        return accessToken;
    }
    private String makeToken(Long expire, UserEntity user){
        Date now = new Date();

        log.info("[makeToken] 실행");
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expire))
                .setSubject(user.getName())
                .claim("id", user.getUserNo())
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())
                .compact();
    }

    public void deleteToken(TokenEntity token){
        log.info("[deleteToken] 실행");
        tokenRepository.delete(token);
    }

    public boolean validationToken(String token) {
        try {
            log.info("[validationToken] 실행");
            Jwts.parser()
                    .setSigningKey(jwtProperties.getSecretKey())
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e){
            log.info("[validationToken] 에러");
            throw new CustomException("세션이 만료되었습니다.", HttpStatus.UNAUTHORIZED);
        }
    }

    public Authentication getAuthentication(String token){
        Claims claims = getClaims(token);
        Set<SimpleGrantedAuthority> authorities =
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));

        return new UsernamePasswordAuthenticationToken(
                new User(claims.getSubject(), "", authorities), token, authorities);
    }
    private Claims getClaims(String token){
        return Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody();
    }
    public Integer getUserNo(String token){
        Claims claims = getClaims(token);
        return claims.get("id", Integer.class);
    }
}