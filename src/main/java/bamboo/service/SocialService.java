package bamboo.service;

import bamboo.config.jwt.TokenProvider;
import bamboo.dto.TokenDTO;
import bamboo.dto.UserCheckDTO;
import bamboo.entity.PeopleEntity;
import bamboo.entity.UserEntity;
import bamboo.exception.CustomException;
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

    public String getGoogleAccessToken(String accessCode, String redirectUri) {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> params = new HashMap<>();

        params.put("code", accessCode);
        params.put("client_id", clientId);
        params.put("client_secret", clientSecret);
        params.put("redirect_uri", redirectUri);
        params.put("grant_type", "authorization_code");

        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, params, Map.class);
        Map<String, String> map = response.getBody();
        return map.get("access_token");
    }

    public UserCheckDTO getGoogleUserInfo(String accessToken) {
        String url = "https://www.googleapis.com/userinfo/v2/me?access_token="+accessToken;
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.getForEntity(url,Map.class);
        Map<String,String> map = response.getBody();

        UserCheckDTO userCheckDTO = UserCheckDTO.builder()
                .email(map.get("email"))
                .name(map.get("name"))
                .build();
        return userCheckDTO;
    }

    public TokenDTO loginResult(UserCheckDTO userCheckDTO) {
        log.info("[checkUser] 실행");
        UserEntity user = checkUser(userCheckDTO);

        return tokenProvider.createToken(user);
    }

    private UserEntity checkUser(UserCheckDTO userCheckDTO) {
        PeopleEntity people = peopleRepository.findById(userCheckDTO.getName())
                .orElseThrow(() -> new IllegalArgumentException("Not Found People userName"));
        if(people.getStatus() == 0){
            throw new CustomException("Not Found Member", userCheckDTO);
        }

        UserEntity userEntity = userRepository.findByName(userCheckDTO.getName())
                .orElseThrow(() -> new IllegalArgumentException("Unexpected userName"));

        if(!userEntity.getEmail().equals(userCheckDTO.getEmail())){
            new IllegalArgumentException("Not Found UserEmail");
        }
        return userEntity;
    }
}
