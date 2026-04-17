package hello.instrumenttrade.domain;

import hello.instrumenttrade.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false, length = 50)
    private String nickname;

    private String phone;
    private String region;
    private String profileImage;

    public static User of(String email, String encodedPassword, String nickname, String region) {
        User user = new User();
        user.email = email;
        user.password = encodedPassword;
        user.nickname = nickname;
        user.region = region;
        return user;
    }

    public void updateProfile(String nickname, String phone, String region, String profileImage) {
        this.nickname = nickname;
        this.phone = phone;
        this.region = region;
        this.profileImage = profileImage;
    }

    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }
}
