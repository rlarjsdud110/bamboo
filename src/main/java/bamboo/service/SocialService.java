package bamboo.service;

import bamboo.config.jwt.TokenProvider;
import bamboo.dto.TokenDTO;
import bamboo.dto.ResponseDTO.UserCheckDTO;
import bamboo.entity.PeopleEntity;
import bamboo.entity.UserEntity;
import bamboo.exception.CustomException;
import bamboo.exception.ErrorCode;
import bamboo.repository.PeopleRepository;
import bamboo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
@Slf4j
@RequiredArgsConstructor
@Service
public class SocialService {

    private final PeopleRepository peopleRepository;
    private final UserRepository userRepository;
    private final TokenProvider  tokenProvider;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;
    @Value("${spring.security.oauth2.client.registration.google.token-url}")
    private String tokenUrl;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirect;

    public String getGoogleAccessToken(String accessCode) {
        log.info("[getGoogleAccessToken]메소드 실행");
        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> params = new HashMap<>();

        params.put("code", accessCode);
        params.put("client_id", clientId);
        params.put("client_secret", clientSecret);
        params.put("redirect_uri", redirect);
        params.put("grant_type", "authorization_code");

        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, params, Map.class);
        Map<String, String> map = response.getBody();
        return map.get("access_token");
    }

    public UserCheckDTO getGoogleUserInfo(String accessToken) {
        log.info("[getGoogleUserInfo]메소드 실행");
        String url = "https://www.googleapis.com/userinfo/v2/me?access_token="+accessToken;
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.getForEntity(url,Map.class);
        Map<String,String> map = response.getBody();

        UserCheckDTO userCheckDTO = UserCheckDTO.builder()
                .email(map.get("email"))
                .name(map.get("name"))
                .build();

        log.info("[getGoogleUserInfo]메소드 종료");
        return userCheckDTO;
    }

    public TokenDTO loginResult(UserCheckDTO userCheckDTO) {
        log.info("[checkUser] 실행");
        UserEntity user = checkUser(userCheckDTO);

        return tokenProvider.createToken(user);
    }

    private UserEntity checkUser(UserCheckDTO userCheckDTO) {
        PeopleEntity people = peopleRepository.findById(userCheckDTO.getName())
                .orElseThrow(() -> new CustomException("우리FISA 학생이 아니군요", ErrorCode.NOT_FISA_STUDENT));
        if(people.getStatus() == 0){
            throw new CustomException("회원가입이 필요합니다.", userCheckDTO, ErrorCode.SIGNUP_REQUIRED);
        }

        UserEntity userEntity = userRepository.findByName(userCheckDTO.getName()).get();

        if(!userEntity.getEmail().equals(userCheckDTO.getEmail())){
            throw new CustomException("이미 다른 이메일로 회원가입되었습니다.", ErrorCode.OTHER_EMAIL_REGISTERED);
        }
        return userEntity;
    }
}
