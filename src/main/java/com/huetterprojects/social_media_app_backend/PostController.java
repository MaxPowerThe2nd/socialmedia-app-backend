package com.huetterprojects.social_media_app_backend;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/posts")
@AllArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Post> createPost(
            @RequestPart("data") @Valid PostRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) throws IOException {

        if (userDetails == null) {
            return new ResponseEntity<>(postService.createPost(request, file, "dev-test-id"), HttpStatus.CREATED);
        }
        Post savedPost = postService.createPost(request, file, userDetails.userId());

        return new ResponseEntity<>(savedPost, HttpStatus.CREATED);
    }


    @GetMapping
    public Page<Post> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String searchCriteria
    ) {
        PostSearchRequest searchRequest = new PostSearchRequest(page, size, searchCriteria);
        return postService.getAllPosts(searchRequest);
    }

    @GetMapping("/{id}")
    public Post getPostById(@PathVariable String id) {
        return postService.getPostById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(
            @PathVariable String id,
            @RequestPart("data") @Valid PostRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws IOException {
        Post updatedPost = postService.updatePost(id, request, file);
        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping("/{id}")
    public void deletePost(@PathVariable String id) {
        postService.deletePost(id);
    }

    @PostMapping("/{id}/like")
    public void likePost(@PathVariable String id) {
        postService.likePost(id);
    }
}