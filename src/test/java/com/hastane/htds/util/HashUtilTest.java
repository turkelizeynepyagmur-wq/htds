package com.hastane.htds.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HashUtilTest {

    @Test
    void testHashPassword() {
        String plainText = "12345";
        String hashed1 = HashUtil.hashPassword(plainText);
        String hashed2 = HashUtil.hashPassword(plainText);

        assertNotNull(hashed1, "Hashlenmiş değer null olmamalı");
        assertFalse(hashed1.isEmpty(), "Hashlenmiş değer boş olmamalı");
        assertEquals(hashed1, hashed2, "Aynı şifreler aynı hash değerini üretmeli");
        assertNotEquals(plainText, hashed1, "Hashlenmiş şifre orijinalinden farklı olmalı");
    }
}
