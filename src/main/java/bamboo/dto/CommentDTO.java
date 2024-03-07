package bamboo.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDTO {

    private Long commentNo;
    private String content;
    private Long postNo;
    private String createdAt;
    private String writer;
    private String writer_img;
}
