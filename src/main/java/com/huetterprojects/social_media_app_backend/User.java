package com.huetterprojects.social_media_app_backend;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document("user")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    private String id;

    @Indexed(unique = true)
    private String username;

    @Indexed(unique = true)
    private String email;

    @JsonIgnore // Das Passwort sollte niemals in JSON-Antworten auftauchen
    private String password;

    private String displayName; // Der Name, der im Profil angezeigt wird
    private String bio;

    private String profilePictureUrl; // Der Key/Pfad zum S3 Bucket

    @Transient
    private String presignedProfilePictureUrl; // Für den S3-Presigned-Link

    private List<String> followerIds; // Liste der IDs von Usern, die mir folgen
    private List<String> followingIds; // Liste der IDs von Usern, denen ich folge

    private boolean isPrivate; // Privates oder öffentliches Konto
    private LocalDateTime createdAt;
    //private UserRole role; // z.B. USER, ADMIN
}