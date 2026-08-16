package com.hastane.htds.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TcKimlikValidatorTest {

    @Test
    void testValidTcKimlik() {
        assertTrue(TcKimlikValidator.gecerliMi("10000000146"), "Geçerli TC kimlik doğrulamadan geçmeli");
    }

    @Test
    void testInvalidLength() {
        assertFalse(TcKimlikValidator.gecerliMi("1234567890"), "10 haneli TC geçersiz sayılmalı");
        assertFalse(TcKimlikValidator.gecerliMi("123456789012"), "12 haneli TC geçersiz sayılmalı");
    }

    @Test
    void testInvalidCharacters() {
        assertFalse(TcKimlikValidator.gecerliMi("1234567890A"), "Harf içeren TC geçersiz sayılmalı");
    }

    @Test
    void testNullOrEmpty() {
        assertFalse(TcKimlikValidator.gecerliMi(null), "Null değer geçersiz sayılmalı");
        assertFalse(TcKimlikValidator.gecerliMi(""), "Boş string geçersiz sayılmalı");
    }
}