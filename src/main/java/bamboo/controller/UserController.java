package bamboo.controller;

import bamboo.dto.TokenDTO;
import bamboo.dto.UserDTO;
import bamboo.exception.CustomException;
import bamboo.service.TokenService;
import bamboo.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.Principal;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/user")
@RestController
@Api(description = "유저 API")
public class UserController {

    private final UserService userService;
    private final TokenService tokenService;

    @ApiOperation(value = "구글 로그인", notes = "스웨거에서 외부 서비스에 대한 테스트가 어려워 구글 로그인 및 회원가입은 테스트가 어렵습니다 test-loin API를 사용해 주세요")
    @GetMapping("/google")
    public ResponseEntity<TokenDTO> googleLogin(@RequestParam("code") String accessCode){
        log.info("[Google Login] 실행");
        TokenDTO tokenDTO = userService.login(accessCode);
        return ResponseEntity.status(HttpStatus.OK).body(tokenDTO);
    }

    @ApiOperation(value = "회원가입", notes = "스웨거에서 외부 서비스에 대한 테스트가 어려워 구글 로그인 및 회원가입은 테스트가 어렵습니다 test-loin API를 사용해 주세요")
    @PostMapping
    public ResponseEntity<TokenDTO> saveUser(@RequestPart(value = "user") UserDTO userDTO,
                                             @RequestPart(required = false, value = "profileImg") MultipartFile multipartFile){
        log.info("[saveUser] 실행");
        TokenDTO tokenDTO;
        try {
            tokenDTO = userService.save(userDTO, multipartFile);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(tokenDTO);
    }

    @ApiOperation(value = "사용자 닉네임 중복확인", notes = "사용자가 입력한 닉네임을 확인합니다.(사용가능 : true, 불가능 : false")
    @GetMapping("/duplicate")
    ResponseEntity<?> duplicate(@RequestParam("nickname") String nickname){
        log.info("[duplicate] 실행");
        Boolean result = userService.duplicate(nickname);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
    @ApiOperation(value = "사용자 정보확인", notes = "사용자 이름을 확인해 정보를 불러옵니다.")
    @GetMapping
    public ResponseEntity<UserDTO> getUserInfo(Principal principal){
        log.info("[getUserInfo] 실행");
        UserDTO user = userService.getUser(principal);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @ApiOperation(value = "사용자 정보수정", notes = "사용자의 정보를 수정합니다.\n" + "이메일, 이름은 프론트에서 수정을 못하게 막았습니다. "+"\n"+"또한 프로필 변경을 안할경우 기존 이미지가 적용됩니다.\n"+"필수 입력 : 생년월일, 이름, 닉네임")
    @PutMapping
    public ResponseEntity<String> updateUserInfo(@ModelAttribute UserDTO userDTO,
                                               @RequestPart(required = false, value = "profileImg") MultipartFile multipartFile,
                                               Principal user){
        log.info("[updateUserInfo] 실행");
        try {
            userService.updateUser(userDTO, multipartFile, user);
            return ResponseEntity.ok().body("수정 완료!");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @ApiOperation(value = "사용자 로그아웃", notes = "로그인할때 발급받은 refreshToken을 넣어주세요")
    @DeleteMapping
    public ResponseEntity<String> logout(@RequestHeader("R-AUTH-TOKEN") String refreshToken) {
        log.info("[logout] 실행");
        userService.logout(refreshToken);
        return ResponseEntity.ok().body("로그아웃!");
    }

    @ApiOperation(value = "토큰 재발급", notes = "로그인할때 발급받은 refreshToken을 넣어주세요")
    @PostMapping("/token")
    public ResponseEntity<String> newAccessToken(@RequestHeader("R-AUTH-TOKEN") String refreshToken){
        log.info("[newAccessToken] 실행");
        String newAccessToken = tokenService.createAccessToken(refreshToken);

        return ResponseEntity.status(HttpStatus.OK).body(newAccessToken);
    }

}
