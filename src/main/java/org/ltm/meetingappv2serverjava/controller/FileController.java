package org.ltm.meetingappv2serverjava.controller;


import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.errors.*;
import io.minio.http.Method;
import org.ltm.meetingappv2serverjava.DTO.Response;
import org.ltm.meetingappv2serverjava.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


@RestController
@RequestMapping("/api/file")
public class FileController {

    private final MinioClient minioClient;
    private final FileService fileService;

    @Value("${minio.bucket.name}")
    private String bucketName;

    public FileController(MinioClient minioClient, FileService fileService) {
        this.minioClient = minioClient;
        this.fileService = fileService;
    }

    @PostMapping("/upload")
    public Response<String> uploadFile(@RequestParam("file") MultipartFile file) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String object = fileService.uploadFile(file);
        String presignedUrl = minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .bucket(bucketName)
                        .object(object)
                        .method(Method.GET)
                        .build()
        );
        return new Response<>(true, presignedUrl);
    }



    @GetMapping("/download/{objectName}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String objectName) {
        try {
            // Retrieve the object from MinIO
            InputStream inputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );

            // Determine the file's content type
            String contentType = Files.probeContentType(new File(objectName).toPath());
            if (contentType == null) {
                contentType = "application/octet-stream"; // Default for unknown types
            }

            // Read file content into a byte array
            byte[] content = inputStream.readAllBytes();

            // Decide how to serve the file
            HttpHeaders headers = new HttpHeaders();

            if (isInlineContent(contentType)) {
                // Inline content (serve directly in the browser)
                headers.setContentType(MediaType.parseMediaType(contentType));
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + objectName + "\"");
            } else {
                // Attachment (force download)
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + objectName + "\"");
            }

            return new ResponseEntity<>(content, headers, HttpStatus.OK);

        } catch (Exception e) {
            throw new RuntimeException("Error occurred while serving file: " + e.getMessage());
        }
    }

    /**
     * Determines if the content type should be served inline.
     *
     * @param contentType the content type of the file
     * @return true if the content type is text, video, or image; false otherwise
     */
    private boolean isInlineContent(String contentType) {
        return contentType.startsWith("text/")
                || contentType.startsWith("image/")
                || contentType.startsWith("video/");
    }

}

