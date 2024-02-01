package bamboo.controller;

import bamboo.dto.CommentDTO;
import bamboo.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/comment")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<Long> saveComment(Principal user, @RequestBody CommentDTO commentDTO){
        long commentNo = commentService.save(user, commentDTO);

        return ResponseEntity.status(HttpStatus.OK).body(commentNo);
    }

    @GetMapping
    public ResponseEntity<List<CommentDTO>> getComment(@RequestParam Long postNo){
        List<CommentDTO> commentList = commentService.getComment(postNo);

        return ResponseEntity.status(HttpStatus.OK).body(commentList);
    }

    @PutMapping
    public ResponseEntity<Long> updateComment(@RequestBody CommentDTO commentDTO){
        long postNo = commentService.update(commentDTO);

        return ResponseEntity.status(HttpStatus.OK).body(postNo);
    }

    @DeleteMapping
    public ResponseEntity<Boolean> deleteComment(@RequestParam Long commentNo){
        boolean result = commentService.delete(commentNo);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PostMapping("/like/{commentNo}")
    public ResponseEntity<Boolean> likeComment(Principal user, @PathVariable Long commentNo){
        boolean result = commentService.like(user, commentNo);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
