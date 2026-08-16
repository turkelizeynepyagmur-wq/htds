package com.hastane.htds.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TcKimlikValidatorTest {

    @Test
    void testValidTcKimlik() {
        // DatabaseSeeder içinde kullanılan ve algoritmayı geçen test TC Kimlik Numarası
        assertTrue(TcKimlikValidator.isValid("10000000146"), "Geçerli TC kimlik doğrulamadan geçmeli");
    }

    @Test
    void testInvalidLength() {
        assertFalse(TcKimlikValidator.isValid("1234567890"), "10 haneli TC geçersiz sayılmalı");
        assertFalse(TcKimlikValidator.isValid("123456789012"), "12 haneli TC geçersiz sayılmalı");
    }

    @Test
    void testInvalidCharacters() {
        assertFalse(TcKimlikValidator.isValid("1234567890A"), "Harf içeren TC geçersiz sayılmalı");
    }

    @Test
    void testNullOrEmpty() {
        assertFalse(TcKimlikValidator.isValid(null), "Null değer geçersiz sayılmalı");
        assertFalse(TcKimlikValidator.isValid(""), "Boş string geçersiz sayılmalı");
    }
}
