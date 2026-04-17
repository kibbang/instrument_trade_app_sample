package hello.instrumenttrade.dto.response;

import hello.instrumenttrade.domain.User;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserResponse {
    private final Long id;
    private final String email;
    private final String nickname;
    private final String phone;
    private final String region;
    private final String profileImage;
    private final LocalDateTime createdAt;

    private UserResponse(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.phone = user.getPhone();
        this.region = user.getRegion();
        this.profileImage = user.getProfileImage();
        this.createdAt = user.getCreatedAt();
    }

    public static UserResponse from(User user) {
        return new UserResponse(user);
    }
}
