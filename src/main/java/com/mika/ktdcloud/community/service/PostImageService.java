package com.mika.ktdcloud.community.service;

import com.mika.ktdcloud.community.entity.Post;
import com.mika.ktdcloud.community.entity.PostImage;
import com.mika.ktdcloud.community.repository.PostImageRepository;
import com.mika.ktdcloud.community.service.file.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@Service
@RequiredArgsConstructor
public class PostImageService {
    private final PostImageRepository postImageRepository;

    @Qualifier("localFileService") // "local" 또는 "s3"
    private final FileService fileService;

    @Transactional
    public PostImage storeFile(MultipartFile file, Post post, int imageOrder) {
        try {
            String imageUrl = fileService.saveFileUrl(file);

            PostImage postImage = PostImage.builder()
                    .originalUrl(imageUrl)
                    .post(post)
                    .imageOrder(imageOrder)
                    .build();

            return postImageRepository.save(postImage);
        } catch (IOException ex) {
            throw new RuntimeException("파일 저장에 실패했습니다.", ex);
        }
    }
}
