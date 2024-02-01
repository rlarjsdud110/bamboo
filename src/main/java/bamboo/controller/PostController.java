package bamboo.controller;


import bamboo.dto.PostDTO;
import bamboo.dto.RequestPostDTO;
import bamboo.exception.CustomException;
import bamboo.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/post")
@RestController
public class PostController {
    //"카테고리별 게시글 조회", notes = "0 : 공지사항, 1 : 잡담, 2 : 정보공유, 3 : 번개"
    private final PostService postService;

    @PostMapping
    public ResponseEntity<Long> savePost(Principal user, @RequestBody RequestPostDTO requestPostDTO){
        Long postNo = postService.save(user, requestPostDTO);
        return ResponseEntity.status(HttpStatus.OK).body(postNo);
    }

    @PostMapping("/img")
    public ResponseEntity<String> saveImage(@RequestPart MultipartFile image){
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

    @GetMapping
    public ResponseEntity<PostDTO> findByPost(@RequestParam Long postNo){
        PostDTO postDTO = postService.findPost(postNo);
        return ResponseEntity.status(HttpStatus.OK).body(postDTO);
    }

    @GetMapping("/{category}")
    public ResponseEntity<List<PostDTO>> findByCategory(@PathVariable int category){
        List<PostDTO> postList = postService.findByCategory(category);

        return ResponseEntity.status(HttpStatus.OK).body(postList);
    }

    @PutMapping
    public ResponseEntity<Long> updatePost(@RequestBody RequestPostDTO requestPostDTO){
        Long postNo = postService.update(requestPostDTO);

        return ResponseEntity.status(HttpStatus.OK).body(postNo);
    }

    @DeleteMapping
    public ResponseEntity<String> delete(@RequestParam Long postNo){
        postService.delete(postNo);
        return ResponseEntity.status(HttpStatus.OK).body("삭제 완료!");
    }

    @PostMapping("/like/{postNo}")
    public ResponseEntity<Boolean> likePost(Principal user, @PathVariable Long postNo){
        boolean result = postService.likePost(user, postNo);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

}
