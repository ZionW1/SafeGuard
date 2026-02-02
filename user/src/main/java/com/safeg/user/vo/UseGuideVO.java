package com.safeg.user.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import org.springframework.web.multipart.MultipartFile;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor

public class UseGuideVO {
    private long id;
    private String title;
    private String content;
    private String author;
    private LocalDate createdAt;
	private LocalDate updatedAt;
}
