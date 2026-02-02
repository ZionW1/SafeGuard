package com.safeg.admin.config; // 마이클의 프로젝트 구조에 맞춰 패키지명 변경!

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter; // ⭐ LocalDate.parse에 필요한 formatter ⭐

@MappedJdbcTypes(JdbcType.DATE) // 데이터베이스의 DATE 타입을 매핑하겠다.
@MappedTypes(LocalDate.class)  // Java의 LocalDate 클래스를 매핑하겠다.
public class LocalDateTypeHandler extends BaseTypeHandler<LocalDate> {

    // 데이터베이스에서 DATE 타입을 읽어올 때 사용하는 포맷
    // DB의 DATE 타입은 보통 'YYYY-MM-DD' 형식이므로 이 패턴을 사용.
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, LocalDate parameter, JdbcType jdbcType) throws SQLException {
        // Java의 LocalDate를 DB의 DATE 타입으로 설정
        ps.setString(i, parameter.format(FORMATTER)); // DB에 문자열 'YYYY-MM-DD' 형식으로 저장
    }

    @Override
    public LocalDate getNullableResult(ResultSet rs, String columnName) throws SQLException {
        // DB 컬럼 이름으로 결과셋에서 읽어올 때
        String dateString = rs.getString(columnName);
        if (dateString != null) {
            return LocalDate.parse(dateString, FORMATTER); // ⭐ 여기에 `YYYY-MM-DD` 패턴 사용 ⭐
        }
        return null;
    }

    @Override
    public LocalDate getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        // DB 컬럼 인덱스로 결과셋에서 읽어올 때
        String dateString = rs.getString(columnIndex);
        if (dateString != null) {
            return LocalDate.parse(dateString, FORMATTER);
        }
        return null;
    }

    @Override
    public LocalDate getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        // CallableStatement에서 읽어올 때 (스토어드 프로시저 등)
        String dateString = cs.getString(columnIndex);
        if (dateString != null) {
            return LocalDate.parse(dateString, FORMATTER);
        }
        return null;
    }
}