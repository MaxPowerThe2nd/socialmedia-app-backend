package com.huetterprojects.social_media_app_backend;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "posts")
public class Post {
    @Id
    private String id;
    private String title;
    private String text;
    private String tags;
    private PostCreator creator;
    private Integer likes;
    private LocalDateTime createdAt;
    private String mediaUrl;
    private MediaType mediaType;
}