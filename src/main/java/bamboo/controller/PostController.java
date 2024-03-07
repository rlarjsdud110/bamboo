package bamboo.controller;


import bamboo.dto.PostDTO;
import bamboo.dto.RequestDTO.RequestPostDTO;
import bamboo.service.PostService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/post")
@RestController
@Api(description = "게시판 API")
public class PostController {
    //"카테고리별 게시글 조회", notes = "0 : 공지사항, 1 : 잡담, 2 : 정보공유, 3 : 번개"
    private final PostService postService;

    @ApiOperation(value = "게시글 작성", notes = "category = 공지사항 : 0, 잡담 : 1, 정보공유 : 2, 번개 : 3  게시글 등록이 완료되면 게시글 번호를 반환합니다.")
    @PostMapping
    public ResponseEntity<Long> savePost(@ApiIgnore Principal user, @RequestBody RequestPostDTO requestPostDTO){
        log.info("[savePost] 실행");
        Long postNo = postService.save(user, requestPostDTO);
        return ResponseEntity.status(HttpStatus.OK).body(postNo);
    }

    @ApiOperation(value = "게시글 이미지 추가", notes = "게시글에 이미지를 추가합니다.")
    @PostMapping("/img")
    public ResponseEntity<String> saveImage(@RequestPart MultipartFile image){
        log.info("[saveImage] 실행");
        String url;
        try {
            url = postService.saveImage(image);
        }
        catch (IOException e){
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(url);
    }
    @ApiOperation(value = "게시글 보기", notes = "게시글 번호로 게시글 정보를 볼 수 있습니다.")
    @GetMapping
    public ResponseEntity<PostDTO> findByPost(@RequestParam Long postNo){
        log.info("[findByPost] 실행");
        PostDTO postDTO = postService.findPost(postNo);
        return ResponseEntity.status(HttpStatus.OK).body(postDTO);
    }

    @ApiOperation(value = "카테고리별 게시글 전체 조회", notes = "공지사항 : 0, 잡담 : 1, 정보공유 : 2, 번개 : 3 ")
    @GetMapping("/{category}")
    public ResponseEntity<List<PostDTO>> findByCategory(@PathVariable int category){
        log.info("[findByCategory] 실행");
        List<PostDTO> postList = postService.findByCategory(category);

        return ResponseEntity.status(HttpStatus.OK).body(postList);
    }

    @ApiOperation(value = "게시글 수정", notes = "title, content, category 수정이 가능합니다.")
    @PutMapping
    public ResponseEntity<Long> updatePost(@RequestBody RequestPostDTO requestPostDTO){
        log.info("[updatePost] 실행");
        Long postNo = postService.update(requestPostDTO);

        return ResponseEntity.status(HttpStatus.OK).body(postNo);
    }

    @ApiOperation(value = "게시글 삭제", notes = "게시글 번호를 받아 삭제를 진행합니다.")
    @DeleteMapping
    public ResponseEntity<String> delete(@RequestParam Long postNo){
        log.info("[delete] 실행");
        postService.delete(postNo);
        return ResponseEntity.status(HttpStatus.OK).body("삭제 완료!");
    }

    @ApiOperation(value = "게시글 좋아요", notes = "게시글 번호를 받고 한번 클릭할 때마다 좋아요(true) 또는 좋아요 취소(false)")
    @PostMapping("/like/{postNo}")
    public ResponseEntity<Boolean> likePost(@PathVariable Long postNo, @ApiIgnore Principal user ){
        log.info("[likePost] 실행");
        boolean result = postService.likePost(user, postNo);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

}
