package com.hastane.htds.model;

/**
 * Sistemdeki 4 rol. Enum kullanmamizin nedeni: roller sabit ve az sayida,
 * derleme zamaninda (compile-time) tip guvenligi saglar - yanlislikla
 * "Yonetci" gibi hatali bir string yazamazsin, IDE seni hemen uyarir.
 */
public enum Rol {
    PERSONEL,
    TEKNISYEN,
    YONETICI,
    ADMIN
}