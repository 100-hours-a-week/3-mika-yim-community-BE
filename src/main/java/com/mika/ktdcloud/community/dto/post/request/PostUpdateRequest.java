package com.mika.ktdcloud.community.dto.post.request;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PostUpdateRequest {
    @Size(max = 25, message = "제목은 최대 25자까지 입력 가능합니다.")
    private String title;

    private String content;

    private String thumbnailUrl;

    private List<String> imageUrls = new ArrayList<>();

    protected PostUpdateRequest() {}

    @Builder
    public PostUpdateRequest(String title, String content, String thumbnailUrl, List<String> imageUrls){
        this.title = title;
        this.content = content;
        this.thumbnailUrl = thumbnailUrl;
        this.imageUrls = imageUrls;
    }
}
