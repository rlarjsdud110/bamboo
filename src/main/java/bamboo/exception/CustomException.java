package bamboo.exception;

import bamboo.dto.ResponseDTO.UserCheckDTO;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class CustomException extends RuntimeException{
    private UserCheckDTO userCheckDTO;
    private int errorCode;

    public CustomException(String message, UserCheckDTO userCheckDTO, ErrorCode errorCode) {
        super(message);
        log.info("[CustomException] CustomException Error");
        log.info("Error Code[{}]", errorCode);
        this.userCheckDTO = userCheckDTO;
        this.errorCode = errorCode.getCode();
    }

    public CustomException(String message, ErrorCode errorCode) {
        super(message);
        log.info("[CustomException] CustomException Error");
        log.info("Error Code[{}]", errorCode.getCode());
        this.errorCode = errorCode.getCode();
    }
}
