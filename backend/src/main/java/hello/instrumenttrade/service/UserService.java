package hello.instrumenttrade.service;

import hello.instrumenttrade.common.exception.ErrorCode;
import hello.instrumenttrade.common.exception.InstrumentTradeException;
import hello.instrumenttrade.domain.User;
import hello.instrumenttrade.dto.request.PasswordChangeRequest;
import hello.instrumenttrade.dto.request.UserProfileUpdateRequest;
import hello.instrumenttrade.dto.response.UserProfileResponse;
import hello.instrumenttrade.dto.response.UserResponse;
import hello.instrumenttrade.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public UserResponse getMe(Long userId) {
        return UserResponse.from(findById(userId));
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(Long userId) {
        return UserProfileResponse.from(findById(userId));
    }

    @Transactional
    public UserResponse updateProfile(Long userId, UserProfileUpdateRequest request) {
        User user = findById(userId);
        user.updateProfile(
                request.getNickname(),
                request.getPhone(),
                request.getRegion(),
                request.getProfileImage()
        );
        return UserResponse.from(user);
    }

    @Transactional
    public void changePassword(Long userId, PasswordChangeRequest request) {
        User user = findById(userId);
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new InstrumentTradeException(ErrorCode.WRONG_PASSWORD);
        }
        user.changePassword(passwordEncoder.encode(request.getNewPassword()));
    }

    private User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new InstrumentTradeException(ErrorCode.USER_NOT_FOUND));
    }
}
