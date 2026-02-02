package com.safeg.user.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
// @AllArgsConstructor
public class CalendarEventVO {

    // private String userId;         // 이벤트 고유 ID (필요시)
    private String start;      // 이벤트 시작 날짜 (yyyy-MM-dd 또는 ISO 8601 형식)
    private String end;      // 이벤트 종료 날짜 (단일 날짜라면 start만 있어도 됨)
    private String status;     // 사용자 정의 필드: 신청/완료 구분 (이걸로 FE에서 스타일 다르게 적용)
    // 다른 FullCalendar 필드도 추가 가능 (url, extendedProps 등)
    // 예를 들어, status 대신 extendedProps에 { status: "APPLIED" } 형태로 넣을 수도 있어
    private String title; // 달력에 표시될 이벤트 제목 (예: "경호 신청", "경호 완료")
    // private String date;
    private String color; // 이벤트 색상 (예: "#629eeb" for 신청, "#28a745" for 완료)
    private String applyDate;
    // private String type; // "APPLIED" or "COMPLETED"

    public CalendarEventVO(String title, String start, String end, String color, String status, String applyDate) {
        this.title = title;
        this.start = start;
        this.end = end; // 또는 start와 동일하게 설정
        this.color = color;
        this.status = status;
        this.applyDate = applyDate;
    }

}
