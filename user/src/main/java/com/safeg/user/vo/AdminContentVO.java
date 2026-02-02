package com.safeg.user.vo;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor

public class AdminContentVO {

    private long adminContentId;
    private String contentType;
    private String title;
    private String content;
    private String author;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private String isActive;

    private String action;

}
