package com.mika.ktdcloud.community.service.file;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;


@Service("s3FileService")
public class S3FileService implements FileService {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.cloud-front.url}")
    private String cdnUrl;

    public S3FileService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public String saveFileUrl(MultipartFile file) throws IOException {
        if(file.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 비어있습니다.");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String storedFilename = "images/" + UUID.randomUUID().toString() + extension;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(storedFilename)
                .contentType(file.getContentType())
                .contentLength(file.getSize())
                .build();

        RequestBody requestBody = RequestBody.fromInputStream(
                file.getInputStream(),
                file.getSize()
        );

        s3Client.putObject(putObjectRequest, requestBody);

        return cdnUrl + "/" + storedFilename;
    }

    @Override
    public void deleteFile(String fileUrl) {
        try {
            String fileKey = fileUrl.substring(cdnUrl.length() + 1);

            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);

        } catch (Exception ex) {
            System.err.println("S3 파일 삭제에 실패했습니다: " + fileUrl);
        }
    }
}
