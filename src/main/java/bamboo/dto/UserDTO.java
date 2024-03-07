package bamboo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class UserDTO {
    private String name;
    @ApiModelProperty(hidden = true)
    private String email;
    @ApiModelProperty(hidden = true)
    private String profileImg;
    private String nickname;
    private String birth;
}