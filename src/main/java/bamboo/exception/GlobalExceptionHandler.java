package bamboo.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<?> exception(CustomException e){
        log.info("[GlobalExceptionHandler] CustomException Error");

        if(e.getUserCheckDTO() != null){
            Map<String, Object>  responseBody = new HashMap<>();
            responseBody.put("message", e.getMessage());
            responseBody.put("name", e.getUserCheckDTO().getName());
            responseBody.put("email", e.getUserCheckDTO().getEmail());
            return ResponseEntity.status(e.getErrorCode()).body(responseBody);
        }else {
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("message", e.getMessage());
            responseBody.put("code", e.getErrorCode());
            return ResponseEntity.status(e.getErrorCode()).body(responseBody);
        }
    }
}
