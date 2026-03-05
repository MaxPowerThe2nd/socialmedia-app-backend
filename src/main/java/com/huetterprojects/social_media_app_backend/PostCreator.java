package com.huetterprojects.social_media_app_backend;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostCreator {
    private String id;
    private String name;
}
