package com.huetterprojects.social_media_app_backend;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PostService   {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final S3Client s3Client;
    private final S3PresignedUrlService s3PresignedUrlService;

    public Post createPost(PostRequest request, MultipartFile mediaFile, String userId) throws IOException {
        // 1. Den echten User aus der Datenbank laden
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User mit ID " + userId + " nicht gefunden"));

        String s3Key = null;
        AWSMediaType mediaType = null;
        if (mediaFile != null && !mediaFile.isEmpty()) {
            s3Key = storeFileInS3(mediaFile);
            mediaType = getMediaType(mediaFile);
        }

        PostCreator creator = PostCreator.builder()
                .id(user.getId())
                .name(user.getUsername())
                .build();

        Post post = new Post();
        post.setTitle(request.title());
        post.setText(request.text());
        post.setTags(request.tags() != null ? request.tags() : List.of());

        post.setMediaUrl(s3Key);
        post.setAWSMediaType(mediaType);

        post.setCreator(creator);
        post.setCreatedAt(LocalDateTime.now());
        post.setLikes(0);

        return postRepository.save(post);
    }

    private static AWSMediaType getMediaType(MultipartFile mediaFile) {
        return Objects.requireNonNull(mediaFile.getContentType()).startsWith("video/") ? AWSMediaType.VIDEO :
                (mediaFile.getContentType().startsWith("image/") ? AWSMediaType.IMAGE : null);
    }

    private String storeFileInS3(MultipartFile mediaFile) throws IOException {
        String fileName = UUID.randomUUID().toString()+" - "+ mediaFile.getOriginalFilename();
        if(!mediaFile.isEmpty()){

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(AWSConfig.BUCKET_NAME)
                    .key(fileName)
                    .build();
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(mediaFile.getBytes()));
        }
        return fileName;
    }

    public Page<Post> getAllPosts(PostSearchRequest request) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");

        // Zugriff erfolgt über die Record-Methoden page(), size() und searchCriteria()
        Pageable pageable = PageRequest.of(request.page(), request.size(), sort);

        var postList = StringUtils.hasText(request.searchCriteria())
                ? postRepository.searchByText(request.searchCriteria(), pageable)
                : postRepository.findAll(pageable);

        postList.forEach(post -> {
            if (post.getMediaUrl() != null) {
                post.setPresignedUrl(s3PresignedUrlService.generatePresignedUrl(post.getMediaUrl()));
            }
        });

        return postList;
    }
    public Post getPostById(String id) {
        var post =  postRepository.findById(id).orElseThrow(() -> new RuntimeException("Post not found"));
        if(post.getMediaUrl()!=null) {
            post.setPresignedUrl(s3PresignedUrlService.generatePresignedUrl(post.getMediaUrl()));
        }
        return post;
    }

    public Post updatePost(String id, PostRequest request, MultipartFile mediaFile) throws IOException {
        Post post = getPostById(id);
        post.setTitle(request.title());
        post.setText(request.text());
        post.setTags(request.tags());

        if (mediaFile != null && !mediaFile.isEmpty()) {
            if (post.getMediaUrl() != null && !post.getMediaUrl().isEmpty()) {
                s3Client.deleteObject(builder ->
                        builder.bucket(AWSConfig.BUCKET_NAME).key(post.getMediaUrl()));
            }

            String fileName = storeFileInS3(mediaFile);
            post.setMediaUrl(fileName);

            AWSMediaType mediaType = getMediaType(mediaFile);
            post.setAWSMediaType(mediaType);
        }
        return postRepository.save(post);
    }

    public void deletePost(String id) {
        Post post = getPostById(id);
        if(post.getMediaUrl()!=null){
            s3Client.deleteObject(builder -> builder.bucket(AWSConfig.BUCKET_NAME).key(post.getMediaUrl()));
        }
        postRepository.deleteById(id);
    }


    public void likePost(String id) {
        postRepository.incrementLikes(id);
    }
}