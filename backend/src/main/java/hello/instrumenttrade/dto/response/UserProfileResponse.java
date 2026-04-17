package hello.instrumenttrade.dto.response;

import hello.instrumenttrade.domain.User;
import lombok.Getter;

@Getter
public class UserProfileResponse {
    private final Long id;
    private final String nickname;
    private final String region;
    private final String profileImage;

    private UserProfileResponse(User user) {
        this.id = user.getId();
        this.nickname = user.getNickname();
        this.region = user.getRegion();
        this.profileImage = user.getProfileImage();
    }

    public static UserProfileResponse from(User user) {
        return new UserProfileResponse(user);
    }
}
