package com.huetterprojects.social_media_app_backend;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;
    private final String bucketName = "socialm-media-backend-storage"; // Oder aus @Value("${aws.s3.bucket}")

    public String uploadFile(MultipartFile file) throws IOException {
        return uploadFile(file, null);
    }
    public String uploadFile(MultipartFile file, String folder) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Datei darf nicht leer sein.");
        }

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename().replace(" ", "_");
        String fullKey = (folder != null && !folder.isEmpty()) ? folder + "/" + fileName : fileName;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fullKey)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(putObjectRequest,
                RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        return fullKey;
    }
}