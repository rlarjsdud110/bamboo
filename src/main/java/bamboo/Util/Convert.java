package bamboo.Util;

import bamboo.dto.CommentDTO;
import bamboo.dto.NoticeDTO;
import bamboo.dto.PostDTO;
import bamboo.dto.RequestDTO.RequestPostDTO;
import bamboo.entity.CommentEntity;
import bamboo.entity.NoticeEntity;
import bamboo.entity.PostEntity;
import bamboo.entity.UserEntity;
import bamboo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.Principal;
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
        UserEntity userEntity = userRepository.findById(postEntity.getWriterNo()).get();

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

    public NoticeDTO toNoticeDTO(NoticeEntity noticeEntity, Principal principal){
        UserEntity userEntity = userRepository.findByName(principal.getName()).get();

        return NoticeDTO.builder()
                .category(noticeEntity.getCategory())
                .content(noticeEntity.getContent())
                .writer(userEntity.getNickname())
                .title(noticeEntity.getTitle())
                .views(noticeEntity.getViews())
                .createdAt(Long.toString(new Date().getTime()))
                .build();
    }
}
