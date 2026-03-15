package com.safeg.user.util;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import lombok.extern.slf4j.Slf4j;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes({String.class})
@Slf4j
public class CryptoTypeHandler extends BaseTypeHandler<String> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
        try {
            // DB에 저장하기 전 암호화
            ps.setString(i, EncryptionUtil.encrypt(parameter));
            ps.setString(i, EncryptionUtil.hash(parameter));
        } catch (Exception e) {
            throw new SQLException("Encryption failed", e);
        }
    }

    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
        try {
            // DB에서 가져온 후 복호화
            return EncryptionUtil.decrypt(rs.getString(columnName));
        } catch (Exception e) {
            return rs.getString(columnName); // 복호화 실패 시 원문 반환 (기존 데이터 호환용)
        }
    }

    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        try {
            return EncryptionUtil.decrypt(rs.getString(columnIndex));
        } catch (Exception e) {
            return rs.getString(columnIndex);
        }
    }

    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        try {
            return EncryptionUtil.decrypt(cs.getString(columnIndex));
        } catch (Exception e) {
            return cs.getString(columnIndex);
        }
    }
}