package com.hastane.htds;

import com.hastane.htds.util.TcKimlikValidator;

public class TcTest {
    public static void main(String[] args) {
        String[] testler = {
                "10000000146", // gecerli format (bilinen test numarasi)
                "12345678901", // gecersiz olmasi beklenen
                "1234567890",  // 10 haneli, gecersiz olmali
                "00000000000" // ilk hane 0, gecersiz olmali
        };

        for (String tc : testler) {
            System.out.println(tc + " -> " + TcKimlikValidator.gecerliMi(tc));
        }
    }
}