package bamboo.dto.RequestDTO;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class RequestPostDTO {
    @ApiModelProperty(hidden = true)
    private Long postNo;
    private String title;
    private String content;
    private int category;

//    public PostEntity toEntity(){
//        return PostEntity.builder()
//                .title(title)
//                .content(content)
//                .category(category)
//                .createdAt(Long.toString(new Date().getTime()))
//                .views(0L)
//                .status(0)
//                .build();
//    }


}
