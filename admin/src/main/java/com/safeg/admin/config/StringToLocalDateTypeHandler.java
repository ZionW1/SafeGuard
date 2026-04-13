package com.safeg.admin.config;
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