package com.safeg.admin.converter;

import com.safeg.admin.util.EncryptionUtil;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class StringCryptoConverter implements AttributeConverter<String, String> {

    @Override
    public String convertToDatabaseColumn(String attribute) {
        try {
            return EncryptionUtil.encrypt(attribute);
        } catch (Exception e) {
            throw new RuntimeException("Data Encryption Error", e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        try {
            return EncryptionUtil.decrypt(dbData);
        } catch (Exception e) {
            throw new RuntimeException("Data Decryption Error", e);
        }
    }
}