package bamboo.service;

import bamboo.Util.Convert;
import bamboo.dto.NoticeDTO;
import bamboo.dto.RequestDTO.NoticeAddDTO;
import bamboo.dto.RequestDTO.NoticeUpdateDTO;
import bamboo.entity.NoticeEntity;
import bamboo.entity.UserEntity;
import bamboo.exception.CustomException;
import bamboo.exception.ErrorCode;
import bamboo.repository.NoticeRepository;
import bamboo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;
    private final Convert convert;
    public Long addNotice(NoticeAddDTO noticeDTO, Principal principal){
        log.info("[addNotice Service] 실행");
        UserEntity userEntity = userRepository.findByName(principal.getName()).get();

        NoticeEntity noticeEntity = NoticeEntity.builder()
                .title(noticeDTO.getTitle())
                .writer(userEntity.getNickname())
                .category(noticeDTO.getCategory())
                .content(noticeDTO.getContent())
                .createdAt(Long.toString(new Date().getTime()))
                .views(0)
                .build();

        noticeRepository.save(noticeEntity);

        return noticeEntity.getNoticeNo();
    }

    public List<NoticeDTO> findAllNotice(Principal principal){
        log.info("[findAllNotice Service] 실행");

        List<NoticeEntity> noticeEntityList = noticeRepository.findAll();
        List<NoticeDTO> noticeList = new ArrayList<>();

        for (NoticeEntity noticeEntity : noticeEntityList) {
            NoticeDTO noticeDTO = convert.toNoticeDTO(noticeEntity, principal);
            noticeList.add(noticeDTO);
        }
        return noticeList;
    }

    public NoticeDTO findNotice(Long id, Principal principal){
        log.info("[findNotice Service] 실행");

        NoticeEntity noticeEntity = noticeRepository.findById(id)
                .orElseThrow(() -> new CustomException("공지사항이 존재하지 않습니다.", ErrorCode.NOTICE_NOT_FOUND));

        noticeEntity.setViews(noticeEntity.getViews() + 1);
        noticeRepository.save(noticeEntity);
        NoticeDTO noticeDTO = convert.toNoticeDTO(noticeEntity, principal);

        return noticeDTO;
    }

    public void updateNotice(Long id, NoticeUpdateDTO noticeDTO){
        log.info("[updateNotice Service] 실행");

        NoticeEntity noticeEntity = noticeRepository.findById(id)
                .orElseThrow(() -> new CustomException("공지사항이 존재하지 않습니다.", ErrorCode.NOTICE_NOT_FOUND));

        noticeEntity.setCategory(noticeDTO.getCategory());
        noticeEntity.setTitle(noticeDTO.getTitle());
        noticeEntity.setContent(noticeDTO.getContent());
        noticeEntity.setCreatedAt(Long.toString(new Date().getTime()));

        noticeRepository.save(noticeEntity);
    }

    public void delete(Long id){
        log.info("[delete Service] 실행");
        NoticeEntity noticeEntity = noticeRepository.findById(id)
                .orElseThrow(() -> new CustomException("공지사항이 존재하지 않습니다.", ErrorCode.NOTICE_NOT_FOUND));

        noticeRepository.delete(noticeEntity);
    }
}
