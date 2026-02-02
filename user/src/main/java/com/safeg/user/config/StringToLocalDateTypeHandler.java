package com.safeg.user.config;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime; // 이 import가 있어야 합니다.
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException; // 예외 처리용 추가

// public class StringToLocalDateTypeHandler extends BaseTypeHandler<LocalDate> {

//     // ⭐⭐⭐ DB에 저장된 날짜 문자열의 정확한 형식으로 패턴을 설정해야 합니다! ⭐⭐⭐
//     // 네 경우 '2025.11.14' 이므로 "yyyy.MM.dd" 입니다.
//     private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd");

//     @Override
//     public void setNonNullParameter(PreparedStatement ps, int i, LocalDate parameter, JdbcType jdbcType) throws SQLException {
//         // Java LocalDate -> DB String 변환 (UPDATE/INSERT 시 사용될 가능성)
//         // 여기서는 SELECT만 다루므로 크게 중요하지 않을 수 있지만, 안전하게 구현
//         ps.setString(i, parameter.format(FORMATTER));
//     }

//     @Override
//     public LocalDate getNullableResult(ResultSet rs, String columnName) throws SQLException {
//         // DB String -> Java LocalDate 변환 (SELECT 시 사용)
//         String s = rs.getString(columnName);
//         if (s == null || s.trim().isEmpty()) { // ⭐ 추가: null 이거나 빈 문자열일 경우 처리
//             return null;
//         }
//         try {
//             return LocalDate.parse(s, FORMATTER);
//         } catch (java.time.format.DateTimeParseException e) {
//             // 파싱 에러 발생 시 로그를 남기거나 다른 방법으로 처리할 수 있습니다.
//             // 여기서는 SQL 예외를 다시 던집니다.
//             throw new SQLException("Cannot parse date string '" + s + "' to LocalDate with pattern '" + FORMATTER + "'", e);
//         }
//     }

//     @Override
//     public LocalDate getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
//         String s = rs.getString(columnIndex);
//         if (s == null || s.trim().isEmpty()) {
//             return null;
//         }
//         try {
//             return LocalDate.parse(s, FORMATTER);
//         } catch (java.time.format.DateTimeParseException e) {
//             throw new SQLException("Cannot parse date string '" + s + "' to LocalDate with pattern '" + FORMATTER + "'", e);
//         }
//     }

//     @Override
//     public LocalDate getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
//         String s = cs.getString(columnIndex);
//         if (s == null || s.trim().isEmpty()) {
//             return null;
//         }
//         try {
//             return LocalDate.parse(s, FORMATTER);
//         } catch (java.time.format.DateTimeParseException e) {
//             throw new SQLException("Cannot parse date string '" + s + "' to LocalDate with pattern '" + FORMATTER + "'", e);
//         }
//     }
// }

public class StringToLocalDateTypeHandler extends BaseTypeHandler<LocalDate> {

    // ⭐⭐⭐ FORMATTER 패턴을 '2025-11-14 00:00:00' 에 맞게 수정합니다! ⭐⭐⭐
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, LocalDate parameter, JdbcType jdbcType) throws SQLException {
        // LocalDate를 DB에 String으로 저장할 때는 시간을 붙여줄 필요가 없어, 날짜만 포맷
        // 단, DB 컬럼 타입에 따라 이 부분을 조정해야 할 수 있습니다.
        // 현재는 SELECT 문제이므로, INSERT/UPDATE 시 이 메서드가 사용된다면 다시 고려
        ps.setString(i, parameter.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    }

    @Override
    public LocalDate getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String s = rs.getString(columnName);
        if (s == null || s.trim().isEmpty()) {
            return null;
        }
        try {
            // ⭐⭐⭐ 시간 정보를 포함한 문자열이므로 LocalDateTime으로 먼저 파싱 후, toLocalDate()로 변환! ⭐⭐⭐
            return LocalDateTime.parse(s, FORMATTER).toLocalDate();
        } catch (java.time.format.DateTimeParseException e) {
            // 어떤 문자열이 넘어오는지, 어떤 패턴을 썼는지 정확히 로그에 남겨 디버깅에 도움
            throw new SQLException(
                "Cannot parse date string '" + s + 
                "' to LocalDate with pattern '" + FORMATTER + 
                "'. Please check DB format and TypeHandler pattern.", e);
        }
    }

    @Override
    public LocalDate getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String s = rs.getString(columnIndex);
        if (s == null || s.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(s, FORMATTER).toLocalDate();
        } catch (java.time.format.DateTimeParseException e) {
            throw new SQLException(
                "Cannot parse date string '" + s + 
                "' to LocalDate with pattern '" + FORMATTER + 
                "'. Please check DB format and TypeHandler pattern.", e);
        }
    }

    @Override
    public LocalDate getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String s = cs.getString(columnIndex);
        if (s == null || s.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(s, FORMATTER).toLocalDate();
        } catch (java.time.format.DateTimeParseException e) {
            throw new SQLException(
                "Cannot parse date string '" + s + 
                "' to LocalDate with pattern '" + FORMATTER + 
                "'. Please check DB format and TypeHandler pattern.", e);
        }
    }
}