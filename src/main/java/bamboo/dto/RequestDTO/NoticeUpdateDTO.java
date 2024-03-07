package bamboo.dto.RequestDTO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoticeUpdateDTO {
    private String title;
    private String content;
    private int category;
    @ApiModelProperty(hidden = true)
    private String createdAt;
}
