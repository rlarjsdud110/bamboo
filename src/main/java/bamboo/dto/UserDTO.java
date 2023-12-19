package bamboo.dto;

import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class UserDTO {
    private String name;
    private String email;
    private String nickname;
    private String birth;
    private String profileImg;
}