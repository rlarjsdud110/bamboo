package bamboo.controller;

import bamboo.dto.TokenDTO;
import bamboo.dto.ResponseDTO.UserCheckDTO;
import bamboo.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/test")
@Api(description = "테스트 로그인 API")
@Slf4j
public class TestLogin {

    private final UserService userService;

    @ApiOperation(value = "테스트 로그인", notes = "테스트용 이메일 : rjsdud1827@gmail.com, name : 김건영\n"+"응답 데이터 accessToken의 값을 Authorize에 넣어주세요")
    @PostMapping("/testLogin")
    public ResponseEntity<TokenDTO> testLogin(@ModelAttribute UserCheckDTO userCheckDTO){
        log.info("[testLogin]메소드 실행");
        TokenDTO tokenDTO = userService.testLogin(userCheckDTO);
        return ResponseEntity.status(HttpStatus.OK).body(tokenDTO);
    }
}
