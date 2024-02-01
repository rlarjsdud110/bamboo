package bamboo.Util;

import bamboo.dto.CommentDTO;
import bamboo.dto.PostDTO;
import bamboo.dto.RequestPostDTO;
import bamboo.entity.CommentEntity;
import bamboo.entity.PostEntity;
import bamboo.entity.UserEntity;
import bamboo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class Convert {

    private final UserRepository userRepository;

    public PostEntity toEntity(RequestPostDTO requestPostDTO){
            return PostEntity.builder()
                    .title(requestPostDTO.getTitle())
                    .content(requestPostDTO.getContent())
                    .category(requestPostDTO.getCategory())
                    .createdAt(Long.toString(new Date().getTime()))
                    .views(0L)
                    .status(0)
                    .build();
    }

    public PostDTO toPostDTO(PostEntity postEntity){
        UserEntity userEntity = userRepository.findById(postEntity.getWriterNo())
                .orElseThrow(() -> new IllegalArgumentException("Not Found postNO : toPostDTO"));

        return PostDTO.builder()
                .postNo(postEntity.getPostNo())
                .writer(userEntity.getNickname())
                .title(postEntity.getTitle())
                .content(postEntity.getContent())
                .createdAt(postEntity.getCreatedAt())
                .category(postEntity.getCategory())
                .views(postEntity.getViews())
                .status(postEntity.getStatus())
                .build();
    }

    public CommentDTO toCommentDTO(CommentEntity commentEntity){

        return CommentDTO.builder()
                .commentNo(commentEntity.getCommentNo())
                .writer(commentEntity.getWriter())
                .createdAt(commentEntity.getCreatedAt())
                .writer_img(commentEntity.getWriter_img())
                .postNo(commentEntity.getPostNo())
                .content(commentEntity.getContent())
                .build();
    }
}
