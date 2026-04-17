package hello.instrumenttrade.service;

import hello.instrumenttrade.common.exception.ErrorCode;
import hello.instrumenttrade.common.exception.InstrumentTradeException;
import hello.instrumenttrade.domain.RefreshToken;
import hello.instrumenttrade.domain.User;
import hello.instrumenttrade.dto.request.LoginRequest;
import hello.instrumenttrade.dto.request.SignupRequest;
import hello.instrumenttrade.dto.request.TokenRefreshRequest;
import hello.instrumenttrade.dto.response.TokenResponse;
import hello.instrumenttrade.dto.response.UserResponse;
import hello.instrumenttrade.repository.RefreshTokenRepository;
import hello.instrumenttrade.repository.UserRepository;
import hello.instrumenttrade.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new InstrumentTradeException(ErrorCode.DUPLICATE_EMAIL);
        }
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new InstrumentTradeException(ErrorCode.DUPLICATE_NICKNAME);
        }

        User user = User.of(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getNickname(),
                request.getRegion()
        );
        return UserResponse.from(userRepository.save(user));
    }

    @Transactional
    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InstrumentTradeException(ErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InstrumentTradeException(ErrorCode.INVALID_CREDENTIALS);
        }

        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getEmail());
        String refreshTokenValue = jwtTokenProvider.generateRefreshToken(user.getId());
        LocalDateTime expiresAt = LocalDateTime.now()
                .plusSeconds(jwtTokenProvider.getRefreshTokenExpiryMs() / 1000);

        refreshTokenRepository.findByUserId(user.getId()).ifPresentOrElse(
                rt -> rt.rotate(refreshTokenValue, expiresAt),
                () -> refreshTokenRepository.save(RefreshToken.of(user, refreshTokenValue, expiresAt))
        );

        return new TokenResponse(accessToken, refreshTokenValue, UserResponse.from(user));
    }

    @Transactional
    public TokenResponse refresh(TokenRefreshRequest request) {
        RefreshToken storedToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new InstrumentTradeException(ErrorCode.INVALID_TOKEN));

        if (storedToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InstrumentTradeException(ErrorCode.EXPIRED_TOKEN);
        }

        User user = storedToken.getUser();
        String newAccessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getEmail());
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user.getId());
        LocalDateTime expiresAt = LocalDateTime.now()
                .plusSeconds(jwtTokenProvider.getRefreshTokenExpiryMs() / 1000);

        storedToken.rotate(newRefreshToken, expiresAt);

        return new TokenResponse(newAccessToken, newRefreshToken, UserResponse.from(user));
    }

    @Transactional
    public void logout(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }
}
