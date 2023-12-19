package bamboo.service;

import bamboo.config.jwt.TokenProvider;
import bamboo.dto.TokenDTO;
import bamboo.dto.UserCheckDTO;
import bamboo.dto.UserDTO;
import bamboo.entity.UserEntity;
import bamboo.repository.PeopleRepository;
import bamboo.repository.UserRepository;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.security.Principal;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PeopleRepository peopleRepository;
    private final AmazonS3 amazonS3;
    private final SocialService oauthService;
    private final TokenProvider tokenProvider;
    private static final String DEFAULT_PATH = "profile/";

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;
    private String imgUrl = "";
    private String profileImg = "";

    public TokenDTO login(String accessCode, String redirectUri){
        log.info("[getGoogleAccessToken] 실행");
        String googleAccessToken = oauthService.getGoogleAccessToken(accessCode, redirectUri);

        log.info("[getGoogleUserInfo] 실행");
        UserCheckDTO userCheckDTO = oauthService.getGoogleUserInfo(googleAccessToken);

        log.info("[loginResult] 실행");
        return oauthService.loginResult(userCheckDTO);
    }

    public TokenDTO save(UserDTO userDTO, MultipartFile multipartFile) throws IOException {
        peopleRepository.findByName(userDTO.getName()).get().setStatus(1);

        if(multipartFile == null){
            imgUrl = amazonS3.getUrl(bucketName, DEFAULT_PATH+"default").toString();
        } else {
            profileImg = DEFAULT_PATH + userDTO.getName();

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(multipartFile.getSize());
            metadata.setContentType(multipartFile.getContentType());

            amazonS3.putObject(bucketName, profileImg, multipartFile.getInputStream(), metadata);
            imgUrl = amazonS3.getUrl(bucketName, profileImg).toString();
        }
        UserEntity user = UserEntity.builder()
                .name(userDTO.getName())
                .email(userDTO.getEmail())
                .nickname(userDTO.getNickname())
                .birth(userDTO.getBirth())
                .profileImg(imgUrl)
                .role(1)
                .build();

        userRepository.save(user);

        return tokenProvider.createToken(user);
    }

    public Boolean dupCheck(String nickname) {
        return userRepository.findByNickname(nickname).isEmpty();
    }

    public UserDTO getUser(Principal principal){
        UserEntity userEntity = userRepository.findByName(principal.getName()).orElseThrow(() ->
                new IllegalArgumentException("Unexpected userName"));
        UserDTO userDTO = UserDTO.builder()
                .name(userEntity.getName())
                .email(userEntity.getEmail())
                .nickname(userEntity.getNickname())
                .birth(userEntity.getBirth())
                .profileImg(userEntity.getProfileImg())
                .build();
        return userDTO;
    }

    @Transactional
    public void updateUser(UserDTO userDTO, MultipartFile multipartFile,  @AuthenticationPrincipal User user) throws IOException{
        if(userDTO.getName().equals(user.getUsername())){
            UserEntity userEntity = userRepository.findByName(userDTO.getName())
                    .orElseThrow(() -> new IllegalArgumentException("Unexpected userName"));
            if(multipartFile != null){
                profileImg = DEFAULT_PATH + userDTO.getName();

                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentLength(multipartFile.getSize());
                metadata.setContentType(multipartFile.getContentType());

                amazonS3.putObject(bucketName, profileImg, multipartFile.getInputStream(), metadata);
                imgUrl = amazonS3.getUrl(bucketName, profileImg).toString();

                userEntity.update(userDTO.getNickname(), userDTO.getBirth(), imgUrl);
            } else {
                userEntity.update(userDTO.getNickname(), userDTO.getBirth(), userEntity.getProfileImg());
            }
        }
    }

    public UserEntity findById(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
    }

}
