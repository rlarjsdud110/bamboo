package bamboo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class NoticeDTO {

    @NotBlank(message = "제목은 필수 항목 입니다.")
    @Size(min = 1, max = 100, message = "제목은 1자 이상 100자 이하로 입력해주세요")
    private String title;

    @NotBlank(message = "내용은 필수 항목 입니다.")
    @Size(min = 1, max = 5000, message = "내용은 1자 이상 5000자 이하로 입력해주세요")
    private String content;

    private String writer;

    private int category;

    private int status;

//입력 받는 객체에는 입력 받지 않는 id와 createdAt 필드는 필요 없다.
}
