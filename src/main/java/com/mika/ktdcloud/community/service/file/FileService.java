package com.mika.ktdcloud.community.service.file;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {
    // 파일 저장
    String saveFileUrl(MultipartFile file) throws IOException;

    // 파일 삭제
    void deleteFile(String fileUrl);
}