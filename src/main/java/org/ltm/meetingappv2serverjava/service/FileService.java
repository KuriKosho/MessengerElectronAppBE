package org.ltm.meetingappv2serverjava.service;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    private final MinioClient minioClient;

    @Value("${minio.bucket.name}")
    private String bucketName;
    public String uploadFile(MultipartFile file) {
        UUID id = UUID.randomUUID();
        String objectName = id + "_" + file.getOriginalFilename();
        try {
            InputStream inputStream = file.getInputStream();
            String contentType = file.getContentType();
            minioClient.putObject(
                    PutObjectArgs.builder().bucket(bucketName).object(objectName).stream(
                                    inputStream, inputStream.available(), -1)
                            .contentType(contentType)
                            .build());
            System.out.println("Upload complete.");

        } catch (Exception e) {
            throw new RuntimeException("Error occurred: " + e.getMessage());
        }
        return objectName;
    }
}
