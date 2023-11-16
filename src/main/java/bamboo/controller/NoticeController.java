package bamboo.controller;

import bamboo.dto.NoticeDeleteDTO;
import bamboo.dto.NoticeDTO;
import bamboo.dto.ResponseError;

import bamboo.entity.NoticeEntity;
import bamboo.exception.AlreadyDeleteException;
import bamboo.exception.NoticeNotFoundException;
import bamboo.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class NoticeController {

    //Repository 를 주입 받는다
    //@RequiredArgsConstructor를 활용해 생성자에서 주입을 받는다
    private final NoticeRepository noticeRepository;
    //공지사항 등록
    @PostMapping("/notice")
    public ResponseEntity<Object> addNotice(@RequestBody @Valid NoticeDTO noticeDTO, Errors errors) {

        if(errors.hasErrors()){

            List<ResponseError> responseErrors = new ArrayList<>();

            errors.getAllErrors().stream().forEach(e -> {
                responseErrors.add(ResponseError.of((FieldError) e));
            });

            return new ResponseEntity<>(errors.getAllErrors(), HttpStatus.BAD_REQUEST);

        }
        // 정상 로직
        noticeRepository.save(NoticeEntity.builder()
                .title(noticeDTO.getTitle())
                .content(noticeDTO.getContent())
                .views(0)
                .createdAt(LocalDateTime.now())
                .writer(noticeDTO.getWriter())
                .category(noticeDTO.getCategory())
                .status(noticeDTO.getStatus())
                .build());

        return ResponseEntity.ok().build();
    }

    //공지사항 전체 조회
//    @GetMapping("/notice")
//    public List<NoticeDTO> notice(){
//        List<NoticeDTO> noticeList = new ArrayList<>();
//
//        return noticeList;
//    }

    @GetMapping("/notice")
    public List<NoticeDTO> notice(){
        List<NoticeDTO> noticeList = new ArrayList<>();

        return noticeList;
    }


//공지사항 상세 조회(조회수 증가 구현)
    @GetMapping("/notice/{id}")
    public NoticeEntity notice(@PathVariable Long id) {

        // .findById() 는 반환타입인 Optional이다, 즉 null을 반환할 수 도 있다.
        // 그래서 Notice를 Optional 로 감싸준다.
        Optional<NoticeEntity> notice = noticeRepository.findById(id);
        if(notice.isPresent()) {

            NoticeEntity notice1 = notice.get();
            notice1.setViews(notice1.getViews() + 1);
            this.noticeRepository.save(notice1);

            return notice1;

//            return notice.get();
        }
        return null;
    }


    /*
    @ExceptionHandler() : 예외가 발생한 요청을 처리하기 위한 핸들러,
    Exception이 발생하게 될 경우 자동으로 정의한 Handler가 실행
     */
    @ExceptionHandler(NoticeNotFoundException.class)
    public ResponseEntity<String> handlerNoticeNotFoundException(NoticeNotFoundException exception){
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }
    // 공지사항 수정(title, content 만 수정)
    @PutMapping("/notice/{id}")
    public void updateNotice(@PathVariable Long id, @RequestBody NoticeDTO noticeDTO) {
        //이 경우 Optional이 아니기에 객체를 바로 받아 사용가능하다
        NoticeEntity noticeEntity = noticeRepository.findById(id).orElseThrow(() -> new NoticeNotFoundException("공지사항의 글이 존재하지 않습니다.2"));
        // 정상 로직 수행
        noticeEntity.setTitle(noticeDTO.getTitle());
        noticeEntity.setContent(noticeDTO.getContent());
        noticeRepository.save(noticeEntity);
    }


    //공지사항 삭제
    @ExceptionHandler(AlreadyDeleteException.class)
    public ResponseEntity<String> handlerAlreadyDeletedException(AlreadyDeleteException exception){
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.OK);
    }
    @DeleteMapping("/notice/{id}")
    public void deleteNotice(@PathVariable Long id){

        NoticeEntity noticeEntity = noticeRepository.findById(id)
                .orElseThrow(() -> new NoticeNotFoundException("공지사항이 없네요 :)"));

        noticeRepository.delete(noticeEntity);

    }

//    @DeleteMapping("/notice")
//    public void deleteNoticeList(@RequestBody NoticeDeleteDTO noticeDeleteDTO) {
//
//        List<NoticeEntity> noticeEntityList = noticeRepository.findByIdIn(noticeDeleteDTO.getIdList())
//                .orElseThrow(() -> new NoticeNotFoundException("공지사항의 글이 존재하지 않습니다."));
//
//        noticeRepository.saveAll(noticeEntityList);
//    }
}