package com.huetterprojects.social_media_app_backend;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final S3Service s3Service;


    public UserResponse createUser(UserRegistrationRequest request, MultipartFile avatar) throws IOException {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username ist bereits vergeben.");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email wird bereits verwendet.");
        }

        User user = User.builder()
                .username(request.getUsername().toLowerCase()) // Normalisierung für Login
                .email(request.getEmail().toLowerCase())
                .password(passwordEncoder.encode(request.getPassword())) // BCrypt Hashing
                .displayName(request.getDisplayName() != null ? request.getDisplayName() : request.getUsername())
                .bio(request.getBio())
                .isPrivate(request.isPrivate())
                .createdAt(LocalDateTime.now())
                .followerIds(new ArrayList<>())
                .followingIds(new ArrayList<>())
                .build();

        if (avatar != null && !avatar.isEmpty()) {
            try {
                String s3Key = s3Service.uploadFile(avatar);
                user.setProfilePictureUrl(s3Key);
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Fehler beim Upload des Avatars");
            }
        }
        User savedUser = userRepository.save(user);
        return mapToResponse(savedUser);
    }


    public UserResponse getUserById(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User nicht gefunden."));
        return mapToResponse(user);
    }


    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .bio(user.getBio())
                .profilePictureUrl(user.getProfilePictureUrl())
                .presignedProfilePictureUrl(user.getPresignedProfilePictureUrl())
                .isPrivate(user.isPrivate())
                .createdAt(user.getCreatedAt())
                .followerCount(user.getFollowerIds() != null ? user.getFollowerIds().size() : 0)
                .followingCount(user.getFollowingIds() != null ? user.getFollowingIds().size() : 0)
                .followerIds(user.getFollowerIds())
                .followingIds(user.getFollowingIds())
                .build();
    }
}