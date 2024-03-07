package bamboo.service;

import bamboo.config.jwt.TokenProvider;
import bamboo.dto.TokenDTO;
import bamboo.dto.ResponseDTO.UserCheckDTO;
import bamboo.dto.UserDTO;
import bamboo.entity.PeopleEntity;
import bamboo.entity.TokenEntity;
import bamboo.entity.UserEntity;
import bamboo.exception.CustomException;
import bamboo.exception.ErrorCode;
import bamboo.repository.PeopleRepository;
import bamboo.repository.TokenRepository;
import bamboo.repository.UserRepository;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
    private final TokenRepository tokenRepository;

    private static final String DEFAULT_PATH = "profile/";

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;
    private String imgUrl = "";
    private String filePath = "";

    public TokenDTO login(String accessCode) {
        log.info("[getGoogleAccessToken] 실행");
        String googleAccessToken = oauthService.getGoogleAccessToken(accessCode);

        log.info("[getGoogleUserInfo] 실행");
        UserCheckDTO userCheckDTO = oauthService.getGoogleUserInfo(googleAccessToken);

        log.info("[loginResult] 실행");
        return oauthService.loginResult(userCheckDTO);
    }

    public TokenDTO save(UserDTO userDTO, MultipartFile img) throws IOException {
        log.info("[save]메소드 실행");
        PeopleEntity peopleEntity = peopleRepository.findByName(userDTO.getName()).orElseThrow(() ->
                new CustomException("우리FISA 학생이 아니군요", ErrorCode.RETRY));
        if (peopleEntity.getStatus() == 1) {
            throw new CustomException("이미 가입한 회원입니다.", ErrorCode.DUPLICATE_EMAIL);
        }

        peopleEntity.setStatus(1);

        if (img == null) {
            imgUrl = amazonS3.getUrl(bucketName, DEFAULT_PATH + "default").toString();
        } else {
            log.info("[AWS] S3이미지 업로드 실행");
            filePath = DEFAULT_PATH + img.getName();

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(img.getSize());
            metadata.setContentType(img.getContentType());

            amazonS3.putObject(bucketName, filePath, img.getInputStream(), metadata);
            imgUrl = amazonS3.getUrl(bucketName, filePath).toString();
            log.info("[AWS] S3이미지 업로드 완료");
        }

        UserEntity user = UserEntity.builder()
                .name(userDTO.getName())
                .email(userDTO.getEmail())
                .nickname(userDTO.getNickname())
                .birth(userDTO.getBirth())
                .profileImg(imgUrl)
                .role(1)
                .build();

        peopleRepository.save(peopleEntity);
        userRepository.save(user);

        return tokenProvider.createToken(user);
    }

    public Boolean duplicate(String nickname) {
        return userRepository.findByNickname(nickname).isEmpty();
    }

    public UserDTO getUser(Principal principal) {
        UserEntity userEntity = userRepository.findByName(principal.getName()).get();

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
    public void updateUser(UserDTO userDTO, MultipartFile multipartFile, Principal user) throws IOException {
        log.info("[updateUser Service] 실행");
        if (userDTO.getName().equals(user.getName())) {
            UserEntity userEntity = userRepository.findByName(userDTO.getName()).get();

            if (multipartFile != null) {
                log.info("[AWS] S3이미지 업로드 실행");
                filePath = DEFAULT_PATH + userDTO.getName();

                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentLength(multipartFile.getSize());
                metadata.setContentType(multipartFile.getContentType());

                amazonS3.putObject(bucketName, filePath, multipartFile.getInputStream(), metadata);
                imgUrl = amazonS3.getUrl(bucketName, filePath).toString();

                userEntity.update(userDTO.getNickname(), userDTO.getBirth(), imgUrl);
            } else {
                userEntity.update(userDTO.getNickname(), userDTO.getBirth(), userEntity.getProfileImg());
            }
        }
    }

    public void logout(String refreshToken) {
        log.info("[tokenProvider.delete] 실행");
        if (tokenProvider.validationToken(refreshToken)) {
            TokenEntity key = tokenRepository.findByRefreshToken(refreshToken)
                    .orElseThrow(() -> new CustomException("토큰이 존재하지 않습니다.", ErrorCode.TOKEN_NOT_FOUND));

            tokenProvider.deleteToken(key);
        }
    }

    public TokenDTO testLogin(UserCheckDTO userCheckDTO) {
        log.info("[testLogin service]메소드 실행");

        if (userCheckDTO.getEmail().equals("rjsdud1827@gmail.com")) {

            UserEntity user = userRepository.findByName(userCheckDTO.getName())
                    .orElseThrow(() -> new CustomException("이메일 다시 입력해주세요", ErrorCode.RETRY));

            return tokenProvider.createToken(user);
        }
        return null;
    }
}
