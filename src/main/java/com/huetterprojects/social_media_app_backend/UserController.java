package com.huetterprojects.social_media_app_backend;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponse> registerUser(
            @RequestPart("data") @Valid UserRegistrationRequest request,
            @RequestPart(value = "avatar", required = false) MultipartFile avatar
    ) throws IOException {

        UserResponse newUser = userService.createUser(request, avatar);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    @GetMapping("/me")
    public UserResponse getCurrentUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return userService.getUserById(userDetails.userId());
    }
}