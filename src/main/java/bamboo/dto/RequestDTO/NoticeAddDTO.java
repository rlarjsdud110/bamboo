package bamboo.dto.RequestDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoticeAddDTO {
    private String title;
    private String content;
    private int category;

}
