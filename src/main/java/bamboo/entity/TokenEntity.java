package bamboo.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RedisHash(value = "token")
public class TokenEntity {

    @Id
    private int userNo;

    @Indexed
    private String refreshToken;

    private String accessToken;

    @TimeToLive
    private Long expiration;

}
