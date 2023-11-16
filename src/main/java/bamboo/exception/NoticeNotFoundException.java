package bamboo.exception;
/*
RuntimeException
- 실행 중에 발생하며 시스템 환경적으로나 인풋 값이 잘못된 경우, 혹은 의도적으로
프로그래머가 잡아내기 위한 조건 등에 부합할 때 발생(throw)되게 만든다
- RuntimeException을 사용하면 따로 예외처리를 해주지 않아도 된다.
 */
public class NoticeNotFoundException extends RuntimeException {
    public NoticeNotFoundException(String message){
        super(message);
    }
}
