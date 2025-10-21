package com.mika.ktdcloud.community.dto.term;

import lombok.Getter;

@Getter
public class TermDTO {
    private final Long id;
    private final String title;
    private final String content;
    private boolean isRequired;

    public TermDTO(Long id, String title, String content, boolean isRequired) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.isRequired = isRequired;
    }
}
