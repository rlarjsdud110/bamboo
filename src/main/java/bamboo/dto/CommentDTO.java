package bamboo.dto;


import lombok.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDTO {

    private Long commentNo;
    private String content;
    private String createdAt;
    private String writer;
    private String writer_img;
    private Long postNo;
}
