package com.huetterprojects.social_media_app_backend;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private String id;
    private String username;
    private String email;
    private String displayName;
    private String bio;
    private String profilePictureUrl;
    private String presignedProfilePictureUrl;


    private int followerCount;
    private int followingCount;

    private List<String> followerIds;
    private List<String> followingIds;

    private boolean isPrivate;
    private LocalDateTime createdAt;
}