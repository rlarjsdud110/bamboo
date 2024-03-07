package bamboo.controller;

import bamboo.dto.RequestDTO.CommentAddDTO;
import bamboo.dto.CommentDTO;
import bamboo.dto.RequestDTO.CommentUpdateDTO;
import bamboo.service.CommentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/comment")
@Api(description = "댓글 API")
public class CommentController {

    private final CommentService commentService;

    @ApiOperation(value = "댓글 작성", notes = "게시글 번호와 댓글 내용을 저장하고 댓글 번호를 반환합니다.(게시글 번호는 프론트에서 넘겨줄 예정입니다.)")
    @PostMapping
    public ResponseEntity<Long> saveComment(@ApiIgnore Principal user, @RequestBody CommentAddDTO commentAddDTO){
        long commentNo = commentService.save(user, commentAddDTO);

        return ResponseEntity.status(HttpStatus.OK).body(commentNo);
    }

    @ApiOperation(value = "댓글 조회", notes = "게시글 번호를 받아 게시글의 모든 댓글을 조회합니다..")
    @GetMapping
    public ResponseEntity<List<CommentDTO>> getComment(@RequestParam Long postNo){
        List<CommentDTO> commentList = commentService.getComment(postNo);

        return ResponseEntity.status(HttpStatus.OK).body(commentList);
    }

    @ApiOperation(value = "댓글 수정", notes = "댓글 번호와 내용을 입력받아 댓글을 수정하고 게시글 번호를 반환합니다. (댓글 번호는 프론트에서 넘겨줄 예정입니다.)")
    @PutMapping
    public ResponseEntity<Long> updateComment(@RequestBody CommentUpdateDTO commentUpdateDTO){
        long postNo = commentService.update(commentUpdateDTO);

        return ResponseEntity.status(HttpStatus.OK).body(postNo);
    }

    @ApiOperation(value = "댓글 삭제", notes = "댓글 번호를 받아 삭제합니다.")
    @DeleteMapping
    public ResponseEntity<Boolean> deleteComment(@RequestParam Long commentNo){
        boolean result = commentService.delete(commentNo);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @ApiOperation(value = "댓글 좋아요", notes = "댓글 번호를 받고 한번 클릭할 때마다 좋아요(true) 또는 좋아요 취소(false)")
    @PostMapping("/like/{commentNo}")
    public ResponseEntity<Boolean> likeComment(@ApiIgnore Principal user, @PathVariable Long commentNo){
        boolean result = commentService.like(user, commentNo);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
