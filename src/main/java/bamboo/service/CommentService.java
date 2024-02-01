package bamboo.service;

import bamboo.Util.Convert;
import bamboo.dto.CommentDTO;
import bamboo.entity.CommentEntity;
import bamboo.entity.CommentLikeEntity;
import bamboo.entity.UserEntity;
import bamboo.exception.CustomException;
import bamboo.repository.CommentLikeRepository;
import bamboo.repository.CommentRepository;
import bamboo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import javax.transaction.Transactional;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final Convert convert;
    private final CommentLikeRepository commentLikeRepository;
    public long save(Principal user, CommentDTO commentDTO){

        UserEntity userEntity = userRepository.findByName(user.getName())
                .orElseThrow(()-> new CustomException("다시 시도해 주세요.", HttpStatus.BAD_REQUEST));

        commentRepository.save(CommentEntity.builder()
                .content(commentDTO.getContent())
                .postNo(commentDTO.getPostNo())
                .createdAt(Long.toString(new Date().getTime()))
                .writer(userEntity.getNickname())
                .build());

        return commentDTO.getPostNo();
    }

    public List<CommentDTO> getComment(@RequestParam Long postNo){
        List<CommentEntity> entityList = commentRepository.findByPostNo(postNo);
        List<CommentDTO> commentList = new ArrayList<>();

        for (CommentEntity commentEntity : entityList){
            commentList.add(convert.toCommentDTO(commentEntity));
        }

        return commentList;
    }

    public Long update(CommentDTO commentDTO){
        CommentEntity commentEntity = commentRepository.findById(commentDTO.getCommentNo())
                .orElseThrow(() -> new CustomException("존재하지 않는 댓글 입니다.", HttpStatus.BAD_REQUEST));

        commentEntity.setContent(commentDTO.getContent());
        commentEntity.setCreatedAt(Long.toString(new Date().getTime()));

        commentRepository.save(commentEntity);

        return commentEntity.getPostNo();
    }

    @Transactional
    public Boolean delete(Long commentNo){
        if(commentNo != null){
            commentLikeRepository.deleteByCommentNo(commentNo);
            commentRepository.deleteById(commentNo);
            return true;
        }
        return false;
    }

    public Boolean like(Principal user, Long commentNo){
        UserEntity userEntity = userRepository.findByName(user.getName())
                .orElseThrow(() -> new CustomException("다시 시도해 주세요.", HttpStatus.BAD_REQUEST));

        Optional<CommentLikeEntity> commentLike = commentLikeRepository.findLikeNoByCommentNoAndUserNo(commentNo,
                userEntity.getUserNo());
        if (!commentLike.isPresent()){
            CommentLikeEntity commentLikeEntity = CommentLikeEntity.builder()
                    .commentNo(commentNo)
                    .userNo(userEntity.getUserNo())
                    .build();

            commentLikeRepository.save(commentLikeEntity);
            return true;
        }
        else {
            commentLikeRepository.deleteById(commentLike.get().getCommentLikeNo());
            return false;
        }
    }
}
