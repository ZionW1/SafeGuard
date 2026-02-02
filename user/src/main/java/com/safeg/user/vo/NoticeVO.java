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

public class NoticeVO {
//     create table if not exists notify
// (
//     id                 bigint auto_increment
//     primary key,
//     created_date       datetime(6)                                                                                null,
//     last_modified_date datetime(6)                                                                                null,
//     business_name      varchar(255)                                                                               null,
//     title              varchar(255)                                                                               null,
//     type               enum ('MOJIP_CREATE', 'APPLICATION', 'CANCEL', 'WINNER', 'INVITATION', 'REPORT', 'REMIND') null
//     );

    private long adminContentId;
    private String businessName;
    private String title;
    private String type;
    private String content;
    private String author;
    private LocalDate createdAt;
    private LocalDate updatedAt;
}
