package com.mika.ktdcloud.community.service.file;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service("localFileService")
public class LocalFileService implements FileService {
    private final Path fileStorageLocation;
    private final String serverUrl;

    public LocalFileService(
            @Value("${file.upload-dir}") String uploadDir,
            @Value("${app.server-url}") String serverUrl
    ) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.serverUrl = serverUrl;
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException ex) {
            throw new RuntimeException("파일을 업로드할 디렉터리를 생성할 수 없습니다.", ex);
        }
    }

    @Override
    public String saveFileUrl(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 비어있습니다.");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String storedFilename = UUID.randomUUID().toString() + extension;

        Path targetLocation = this.fileStorageLocation.resolve(storedFilename);
        file.transferTo(targetLocation);

        return serverUrl + "/static/images/" + storedFilename;
    }

    @Override
    public void deleteFile(String fileUrl) {
        try {
            String filename = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            Path filePath = this.fileStorageLocation.resolve(filename).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            System.err.println("파일 삭제에 실패했습니다: " + fileUrl);
        }
    }
}
