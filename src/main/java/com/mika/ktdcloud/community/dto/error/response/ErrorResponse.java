package com.mika.ktdcloud.community.dto.error.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ErrorResponse {
    private boolean success;
    private final int status;
    private final String code;
    private final String message;
}