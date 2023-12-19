package bamboo.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "user")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_no")
    private int userNo;

    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "nickname", nullable = false)
    private String nickname;
    @Column(name = "email", nullable = false)
    private String email;
    @Column(name = "profile_img")
    private String profileImg;
    @Column(name = "birth", nullable = false)
    private String birth;
    @Column(name = "role")
    private int role;

    @Builder
    public UserEntity(String name, String email, String nickname, String birth, String profileImg, int role){
        this.name = name;
        this.email = email;
        this.nickname = nickname;
        this.birth = birth;
        this.profileImg = profileImg;
        this.role = role;
    }

    public UserEntity update(String nickname, String birth, String profileImg){
        this.nickname = nickname;
        this.birth = birth;
        this.profileImg = profileImg;

        return this;
    }

}

