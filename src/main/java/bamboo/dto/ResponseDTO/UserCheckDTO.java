package bamboo.dto.ResponseDTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class UserCheckDTO {
    private String name;
    private String email;
}
