package bamboo.exception;

public enum ErrorCode {
    RETRY(400),
    SIGNUP_REQUIRED(452),
    DUPLICATE_EMAIL(453),
    OTHER_EMAIL_REGISTERED(455),
    NOT_FISA_STUDENT(454),
    POST_NOT_FOUND(462),
    COMMENT_NOT_FOUND(472),
    TOKEN_NOT_FOUND(482),
    SESSION_EXPIRED(401),
    NOTICE_NOT_FOUND(492);

    private final int code;

    ErrorCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
