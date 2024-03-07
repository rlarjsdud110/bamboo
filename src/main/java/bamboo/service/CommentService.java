package bamboo.service;

import bamboo.Util.Convert;
import bamboo.dto.RequestDTO.CommentAddDTO;
import bamboo.dto.CommentDTO;
import bamboo.dto.RequestDTO.CommentUpdateDTO;
import bamboo.entity.CommentEntity;
import bamboo.entity.CommentLikeEntity;
import bamboo.entity.UserEntity;
import bamboo.exception.CustomException;
import bamboo.exception.ErrorCode;
import bamboo.repository.CommentLikeRepository;
import bamboo.repository.CommentRepository;
import bamboo.repository.PostRepository;
import bamboo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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
    private final PostRepository postRepository;
    public long save(Principal user, CommentAddDTO commentAddDTO){

        UserEntity userEntity = userRepository.findByName(user.getName())
                .orElseThrow(()-> new CustomException("다시 시도해 주세요.", ErrorCode.RETRY));

        if(!postRepository.existsById(commentAddDTO.getPostNo())) {
            throw new CustomException("다시 시도해 주세요.", ErrorCode.RETRY);
        }

        commentRepository.save(CommentEntity.builder()
                .content(commentAddDTO.getContent())
                .postNo(commentAddDTO.getPostNo())
                .createdAt(Long.toString(new Date().getTime()))
                .writer(userEntity.getNickname())
                .build());

        return commentAddDTO.getPostNo();
    }

    public List<CommentDTO> getComment(@RequestParam Long postNo){
        List<CommentEntity> entityList = commentRepository.findByPostNo(postNo);
        List<CommentDTO> commentList = new ArrayList<>();

        if(commentList.isEmpty()){
            throw new CustomException("존재하지 않는 게시글 입니다.", ErrorCode.POST_NOT_FOUND);
        }

        for (CommentEntity commentEntity : entityList){
            commentList.add(convert.toCommentDTO(commentEntity));
        }

        return commentList;
    }

    public Long update(CommentUpdateDTO commentUpdateDTO){
        CommentEntity commentEntity = commentRepository.findById(commentUpdateDTO.getCommentNo())
                .orElseThrow(() -> new CustomException("존재하지 않는 댓글 입니다.", ErrorCode.COMMENT_NOT_FOUND));

        commentEntity.setContent(commentUpdateDTO.getContent());
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
                .orElseThrow(() -> new CustomException("다시 시도해 주세요.", ErrorCode.RETRY));

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
