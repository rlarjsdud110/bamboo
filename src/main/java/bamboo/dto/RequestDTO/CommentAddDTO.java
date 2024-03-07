package bamboo.dto.RequestDTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentAddDTO {
    private Long postNo;
    private String content;
}
