package bamboo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.FieldError;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseError {
    private String field;
    private String message;

    public void add(List<ResponseError> responseErrors) {
    }

    public static ResponseError of(FieldError e) {
        return ResponseError.builder()
                .field((e).getField())
                .message(e.getDefaultMessage())
                .build();

    }
}
