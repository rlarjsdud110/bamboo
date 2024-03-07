package bamboo.dto;

import lombok.*;
@Builder
@Getter
@Setter
public class NoticeDTO {
    private String title;
    private String content;
    private String writer;
    private int category;
    private int views;
    private String createdAt;
}
