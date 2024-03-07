package bamboo.controller;

import bamboo.dto.NoticeDTO;
import bamboo.dto.RequestDTO.NoticeAddDTO;
import bamboo.dto.RequestDTO.NoticeUpdateDTO;
import bamboo.service.NoticeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/notice")
@Api(description = "공지사항 API")
public class NoticeController {

    private final NoticeService noticeService;
    @PostMapping
    @ApiOperation(value = "공지사항 등록", notes = "공지사항을 등록할 수 있습니다.")
    public ResponseEntity<Long> addNotice(@RequestBody NoticeAddDTO noticeDTO, @ApiIgnore Principal principal) {
        log.info("[addNotice] 실행");
        long noticeNo = noticeService.addNotice(noticeDTO, principal);

        return ResponseEntity.status(HttpStatus.CREATED).body(noticeNo);
    }

    @GetMapping
    @ApiOperation(value = "공지사항 전체 조회", notes = "모든 공지사항을 조회합니다.")
    public ResponseEntity<List<NoticeDTO>> findAllNotice(@ApiIgnore Principal principal){
        log.info("[findAllNotice] 실행");
        List<NoticeDTO> noticeList = noticeService.findAllNotice(principal);

        return ResponseEntity.status(HttpStatus.OK).body(noticeList);
    }

    @GetMapping("{id}")
    @ApiOperation(value = "공지사항 상세 조회", notes = "공지사항 등록API 에서 반환받은 번호(게시글 고유번호)를 넣어주세요")
    public ResponseEntity<NoticeDTO> findNotice(@PathVariable Long id, @ApiIgnore Principal principal){
        log.info("[findNotice] 실행");
        NoticeDTO noticeDTO = noticeService.findNotice(id, principal);
        return ResponseEntity.status(HttpStatus.OK).body(noticeDTO);
    }

    @PutMapping("{id}")
    @ApiOperation(value = "공지사항 수정", notes = "공지사항 등록API 에서 반환받은 번호(게시글 고유번호)와 수정할 값을 넣어주세요")
    public ResponseEntity<String> updateNotice(@PathVariable Long id, @RequestBody NoticeUpdateDTO noticeDTO){
        log.info("[updateNotice] 실행");
        noticeService.updateNotice(id, noticeDTO);
        return ResponseEntity.status(HttpStatus.OK).body("수정 완료!");
    }

    @DeleteMapping("{id}")
    @ApiOperation(value = "공지사항 삭제", notes = "공지사항 등록API 에서 반환받은 번호(게시글 고유번호)를 사용해 공지사항을 삭제합니다.")
    public ResponseEntity<String> delete(@PathVariable Long id){
        log.info("[delete] 실행");

        noticeService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body("삭제 완료!");
    }
}