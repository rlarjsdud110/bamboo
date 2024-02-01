package bamboo.entity;

import lombok.*;
import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "post")
public class PostEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_no")
    private Long postNo;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "created_at")
    private String createdAt;

    @Column(name = "category")
    private int category;

    @Column(name = "writer_no")
    private int writerNo;

    @Column(name = "views")
    private Long views;

    @Column(name = "status")
    private int status;

    public PostEntity update(String title, String content, int category){
        this.title = title;
        this.content = content;
        this.category = category;
        this.createdAt = Long.toString(new Date().getTime());
        return this;
    }

//    public static PostDTO toPostDTO(PostEntity postEntity){
//        return PostDTO.builder()
//                .postNo(postEntity.getPostNo())
//                .writer(null)
//                .title(postEntity.getTitle())
//                .content(postEntity.getContent())
//                .createdAt(postEntity.getCreatedAt())
//                .category(postEntity.getCategory())
//                .views(postEntity.getViews())
//                .status(postEntity.getStatus())
//                .likes(0)
//                .build();
//    }
}
