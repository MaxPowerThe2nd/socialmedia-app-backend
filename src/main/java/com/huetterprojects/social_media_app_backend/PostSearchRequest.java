package com.huetterprojects.social_media_app_backend;

public record PostSearchRequest(
        int page,
        int size,
        String searchCriteria
) {}