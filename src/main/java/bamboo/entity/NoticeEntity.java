package bamboo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity // pk 지정이 필요
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class NoticeEntity {

    @Id // pk 설정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // GeneratedValue, 값이 어떻게 만들어지는지 설정, GenerationType.IDENTITY, 자동으로 값 만들어지게 한다
    @Column(name = "notice_no")
    private long id; //왜 이거를 noticeNo로 바꾸면 계속 오류가 날까...

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "created_at")
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "category")
    private int category;

    @Column(name = "writer")
    private String writer;

    //초기값 0
    @Column(name = "views")
    private int views;

    @Column(name = "status")
    private int status;
}
