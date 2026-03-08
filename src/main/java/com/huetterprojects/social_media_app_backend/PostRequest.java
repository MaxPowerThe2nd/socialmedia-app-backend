package com.huetterprojects.social_media_app_backend;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

public record PostRequest(

        @NotBlank(message = "Ein Titel ist erforderlich")
        @Size(min = 3, max = 100, message = "Der Titel muss zwischen 3 und 100 Zeichen lang sein")
        String title,

        @Size(max = 2200, message = "Die Beschreibung ist zu lang")
        String text,

        List<String> tags
) {}