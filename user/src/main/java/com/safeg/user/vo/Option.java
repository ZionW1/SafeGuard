package com.safeg.user.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Getter, Setter, RequiredArgsConstructor, ToString, EqualsAndHashCode 포함
@AllArgsConstructor
@NoArgsConstructor // 직접 만든 생성자 대신 이걸 쓰세요!
@Builder // 필요한 경우 객체 생성을 편하게 도와줍니다
public class Option {
    
    @Builder.Default // 빌더 사용 시 기본값 설정
    private String keyword = ""; // 검색어
    
    private int code; // 검색 옵션 코드
    private int orderCode; // 순서 옵션 코드

    // 직접 작성하셨던 생성자는 지우거나 아래처럼 유지해도 되지만, 
    // 위 어노테이션들이 있으면 굳이 없어도 됩니다.
}
/**
 * 검색 옵션
 * - keyword : 검색어
 * - code : 옵션코드
 * 0 : 전체
 * 1 : 제목
 * 2 : 내용
 * 3 : 제목 + 내용
 * 4 : 작성자
 * 
 * - orderCode : 순서 옵션 코드
 * 0 : 최신순 (등록일자)
 * 1 : 제목순
 */

// @Data
// @AllArgsConstructor
// public class Option {
//     String keyword; // 검색어
//     int code; // 검색 옵션 코드
//     int orderCode; // 순서 옵션 코드
//     public Option() {
//         this.keyword = "";
//         this.code = 0;
//     }
// }
