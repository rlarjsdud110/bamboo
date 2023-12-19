package bamboo.controller;

import bamboo.dto.TokenDTO;
import bamboo.dto.UserDTO;
import bamboo.exception.CustomException;
import bamboo.service.TokenService;
import bamboo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.Principal;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("user")
@RestController
public class UserController {

    private final UserService userService;
    private final TokenService tokenService;

    @GetMapping("/google")
    public ResponseEntity<TokenDTO> googleLogin(@RequestParam("code") String accessCode, @RequestParam("redirect") String redirectUri){
        log.info("[Google Login] 실행");
        TokenDTO tokenDTO = userService.login(accessCode, redirectUri);
        return ResponseEntity.status(HttpStatus.OK).body(tokenDTO);
    }

    @PostMapping
    public ResponseEntity<TokenDTO> saveUser(@RequestPart(value = "user") UserDTO userDTO,
                                         @RequestPart(required = false, value = "profileImg") MultipartFile multipartFile){
        log.info("[saveUser] 실행 UserDTO = {} ",userDTO);
        TokenDTO tokenDTO;
        try {
            tokenDTO = userService.save(userDTO, multipartFile);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(tokenDTO);
    }

    @GetMapping("/duplicate")
    ResponseEntity<?> dupCheck(@RequestParam("nickname") String nickname){
        log.info("[duplicate] 실행");
        Boolean result = userService.dupCheck(nickname);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping
    public ResponseEntity<UserDTO> getUserInfo(Principal principal){
        log.info("[getUserInfo] 실행");
        UserDTO user = userService.getUser(principal);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @PutMapping
    public ResponseEntity<Void> updateUserInfo(@RequestPart(value = "user") UserDTO userDTO,
                                               @RequestPart(required = false, value = "profileImg") MultipartFile multipartFile,
                                               @AuthenticationPrincipal User user){
        log.info("[updateUserInfo] 실행");
        try {
            userService.updateUser(userDTO, multipartFile, user);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        log.info("[logout] 실행");
        tokenService.logout(request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/token")
    public ResponseEntity<String> newAccessToken(HttpServletRequest request){
        log.info("[newAccessToken] 실행");
        String newAccessToken = tokenService.createAccessToken(request);

        return ResponseEntity.status(HttpStatus.OK).body(newAccessToken);
    }

    @ExceptionHandler
    public ResponseEntity<?> exception(CustomException e){
        log.info("[exception] CustomException error");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getUserCheckDTO());
    }
}
