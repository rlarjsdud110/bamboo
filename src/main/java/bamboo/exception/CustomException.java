package bamboo.exception;

import bamboo.dto.UserCheckDTO;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{
    private UserCheckDTO userCheckDTO;

    public CustomException(String message, UserCheckDTO userCheckDTO){
        super(message);
        this.userCheckDTO = userCheckDTO;
    }
}
