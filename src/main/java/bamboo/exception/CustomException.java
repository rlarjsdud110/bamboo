package bamboo.exception;

import bamboo.dto.UserCheckDTO;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
@Getter
public class CustomException extends RuntimeException{
    private UserCheckDTO userCheckDTO;
    private HttpStatus httpStatus;

    public CustomException(String message, UserCheckDTO userCheckDTO, HttpStatus httpStatus) {
        super(message);
        log.info("[CustomException] CustomException Error");

        this.userCheckDTO = userCheckDTO;
        this.httpStatus = httpStatus;
    }

    public CustomException(String message, HttpStatus httpStatus) {
        super(message);
        log.info("[CustomException] CustomException Error");

        this.httpStatus = httpStatus;
    }
}
