package bamboo.service;

import bamboo.Util.Convert;
import bamboo.dto.PostDTO;
import bamboo.dto.RequestPostDTO;
import bamboo.entity.CommentEntity;
import bamboo.entity.PostEntity;
import bamboo.entity.PostLikeEntity;
import bamboo.entity.UserEntity;
import bamboo.exception.CustomException;
import bamboo.repository.*;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;

    private final AmazonS3 amazonS3;
    private final Convert convert;
    private static final String DEFAULT_PATH = "post/";
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public Long save(Principal user, RequestPostDTO requestPostDTO){
        UserEntity userEntity = userRepository.findByName(user.getName())
                .orElseThrow(() -> new CustomException("우리FISA 학생이 아니군요", HttpStatus.BAD_REQUEST));

        PostEntity postEntity = convert.toEntity(requestPostDTO);
        postEntity.setWriterNo(userEntity.getUserNo());

        postRepository.save(postEntity);
        return postEntity.getPostNo();
    }

    public String saveImage(@RequestPart MultipartFile image) throws IOException {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(image.getSize());
        metadata.setContentType(image.getContentType());

        String filePath = DEFAULT_PATH + image.getName();
        amazonS3.putObject(bucketName, filePath, image.getInputStream(), metadata);

        return amazonS3.getUrl(bucketName, filePath).toString();
    }

    public PostDTO findPost(Long postNo){
        PostEntity postEntity = postRepository.findById(postNo)
                .orElseThrow(() -> new CustomException("존재하지 않는 게시글 입니다.", HttpStatus.BAD_REQUEST));

        postEntity.setViews(postEntity.getViews()+1);
        postRepository.save(postEntity);

        PostDTO postDTO = convert.toPostDTO(postEntity);

        postDTO.setLikes(postLikeRepository.countByPostNo(postNo));
        return postDTO;
    }

    public List<PostDTO> findByCategory(int category){
        List<PostEntity> entityList = postRepository.findByCategory(category);
        List<PostDTO> postList = new ArrayList<>();

        for(PostEntity postEntity : entityList){
            int likes = postLikeRepository.countByPostNo(postEntity.getPostNo());
            PostDTO postDTO = convert.toPostDTO(postEntity);
            postDTO.setLikes(likes);
            postList.add(postDTO);
        }

        return postList;
    }

    public Long update(RequestPostDTO requestPostDTO){
        PostEntity postEntity = postRepository.findById(requestPostDTO.getPostNo())
                .orElseThrow(()-> new CustomException("존재하지 않는 게시글 입니다.", HttpStatus.BAD_REQUEST));

        postEntity.update(requestPostDTO.getTitle(),
                requestPostDTO.getContent(),
                requestPostDTO.getCategory());

        postRepository.save(postEntity);

        return postEntity.getPostNo();
    }

    @Transactional
    public void delete(Long postNo){
        List<CommentEntity> comments = commentRepository.findByPostNo(postNo);

        for (CommentEntity comment : comments){
            commentLikeRepository.deleteByCommentNo(comment.getCommentNo());
        }

        commentRepository.deleteAll(comments);
        postLikeRepository.deleteByPostNo(postNo);
        postRepository.deleteById(postNo);
    }

    public Boolean likePost(Principal user, Long postNo){
        UserEntity userEntity = userRepository.findByName(user.getName())
                .orElseThrow(() -> new CustomException("다시 시도해 주세요.", HttpStatus.BAD_REQUEST));

        Optional<PostLikeEntity> postLike = postLikeRepository.findLikeNoByPostNoAndUserNo(postNo,
                userEntity.getUserNo());
        if (!postLike.isPresent()){
            PostLikeEntity postLikeEntity = PostLikeEntity.builder()
                    .postNo(postNo)
                    .userNo(userEntity.getUserNo())
                    .build();

            postLikeRepository.save(postLikeEntity);
            return true;
        }
        else {
            postLikeRepository.deleteById(postLike.get().getPostLikeNo());
            return false;
        }
    }
}
