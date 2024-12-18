package io.ndk.backend.service.impl;

import io.ndk.backend.Mappers.Mapper;
import io.ndk.backend.dto.request.AccountSignUp;
import io.ndk.backend.dto.request.SignInRequest;
import io.ndk.backend.dto.response.SignInResponse;
import io.ndk.backend.entity.User;
import io.ndk.backend.handler.BusinessErrorCodes;
import io.ndk.backend.handler.CustomException;
import io.ndk.backend.repository.UserRepository;
import io.ndk.backend.service.JwtService;
import io.ndk.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final Mapper<User, AccountSignUp> mapper;


    @Override
    public AccountSignUp signUp(AccountSignUp dto) {
        userRepository.findByEmail(dto.getEmail()).ifPresent(user -> {throw new CustomException(BusinessErrorCodes.EMAIL_IS_USED);});
        dto.setPassword(passwordEncoder.encode(dto.getPassword()));
        User user = User.builder()
                .password(dto.getPassword())
                .email(dto.getEmail())
                .build();
        User savedUser = userRepository.save(user);
        return mapper.mapTo(savedUser);

    }

    @Override
    public SignInResponse signIn(SignInRequest dto) {
        User user = userRepository.findByEmail(dto.getEmail()).orElseThrow(() -> new CustomException(BusinessErrorCodes.NO_SUCH_EMAIL));
//        if(!user.isEnabled())
//            throw new MessagingException("account is not active");
//
//        if(user.isAccountLocked())
//            throw new MessagingException("account is locked");
        userRepository.save(user);
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));
        if (authentication.isAuthenticated()) {
            return
                    SignInResponse.builder()
                            .id(user.getId())
                            .email(dto.getEmail())
                            .build();
        } else {
            throw new CustomException(BusinessErrorCodes.BAD_CREDENTIALS);
        }
    }

    @Override
    public void logout(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(BusinessErrorCodes.NO_SUCH_EMAIL));
        userRepository.save(user);
    }
}
